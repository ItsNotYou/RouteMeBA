package de.unipotsdam.nexplorer.client.android.js;

import de.unipotsdam.nexplorer.client.android.callbacks.AjaxResult;
import de.unipotsdam.nexplorer.client.android.commons.Location;
import de.unipotsdam.nexplorer.client.android.net.CollectItem;
import de.unipotsdam.nexplorer.client.android.net.RequestPing;
import de.unipotsdam.nexplorer.client.android.net.RestMobile;
import de.unipotsdam.nexplorer.client.android.net.SendLocation;
import de.unipotsdam.nexplorer.client.android.rest.GameStatus;
import de.unipotsdam.nexplorer.client.android.rest.Item;
import de.unipotsdam.nexplorer.client.android.rest.LoginAnswer;
import de.unipotsdam.nexplorer.client.android.rest.Neighbour;
import de.unipotsdam.nexplorer.client.android.support.CollectObserver;
import de.unipotsdam.nexplorer.client.android.support.LocationObserver;
import de.unipotsdam.nexplorer.client.android.support.LoginObserver;
import de.unipotsdam.nexplorer.client.android.support.PingObserver;
import de.unipotsdam.nexplorer.client.android.support.RangeObserver;
import de.unipotsdam.nexplorer.client.android.ui.UI;

/**
 * mainly legacy code from Tobias Moebert has been adapted to work with a java backend and gwt client wrapper
 * 
 * @author Julian Dehne
 */
public class FunctionsMobile implements PositionWatcher {

	private final MapRelatedTasks mapTasks;
	private final Intervals intervals;
	private final UI ui;
	private final AppWrapper app;

	// TODO: Parameter flexibilisieren
	private double minAccuracy = 11;

	private boolean gameStatusRequestExecutes = false;

	private Integer playerId = null;
	private double battery = 100;
	private java.util.Map<Integer, Neighbour> neighbours;
	private int neighbourCount = 0;
	private int score;
	private int playerRange;
	private java.util.Map<Integer, Item> nearbyItems;
	private Object nextItemDistance;
	private boolean itemInCollectionRange;
	private boolean hasRangeBooster;
	private String hint = "Achte auf die Hinweise!";

	private boolean gameIsRunning;
	private boolean gameExists;
	private boolean gameDidExist = true; // die semantik davon, dass es mal ein Spiel gegeben
	// hat, ist mir unklar ... es hat hat schon immer ein
	// Spiel gegeben!
	private long remainingPlayingTime;
	private int itemCollectionRange;
	private boolean gameDidEnd = false;

	private Location currentLocation;
	private RestMobile rest;

	private final LocationObserver locationObserver;
	private final LoginObserver loginObserver;
	private final PingObserver pingObserver;
	private final CollectObserver collectObserver;
	private final RangeObserver rangeObserver;

	public FunctionsMobile(UI ui, AppWrapper app, Intervals intervals, MapRelatedTasks mapTasks, RestMobile rest, RadiusBlinker blinker) {
		this.mapTasks = mapTasks;
		this.intervals = intervals;
		this.app = app;
		this.ui = ui;
		this.rest = rest;

		SendLocation sendLocation = new SendLocation(rest);
		CollectItem collectItem = new CollectItem(rest, ui);
		RadiusBlinker radiusBlinker = blinker;
		RequestPing requestPing = new RequestPing(rest);

		this.locationObserver = new LocationObserver();
		this.locationObserver.add(sendLocation);
		this.locationObserver.add(radiusBlinker);
		this.locationObserver.add(requestPing);

		this.loginObserver = new LoginObserver();
		this.loginObserver.add(sendLocation);
		this.loginObserver.add(collectItem);
		this.loginObserver.add(requestPing);

		this.pingObserver = new PingObserver();
		this.pingObserver.add(radiusBlinker);
		this.pingObserver.add(requestPing);

		this.collectObserver = new CollectObserver();
		this.collectObserver.add(collectItem);

		this.rangeObserver = new RangeObserver();
		this.rangeObserver.add(radiusBlinker);

		intervals.ensurePositionWatch(this);
	}

	/**
	 * Dise Funktion wird zun‰chst aufgerufen sie loggt den spier ein und zeigt bei existierenden Spiel eine Karte
	 * 
	 * @param name
	 * @param isMobile
	 */
	public void loginPlayer(final String name, final boolean isMobile) {
		if (name != "") {
			ui.labelButtonForLogin();

			rest.login(name, isMobile, new AjaxResult<LoginAnswer>() {

				@Override
				public void success(LoginAnswer data) {
					loginSuccess(data);
				}

				@Override
				public void error() {
					ui.showLoginError("Exception wurde ausgelˆﬂt - Kein Spiel gestartet?");
				}
			});
		}
	}

	/**
	 * callback for the geolocation
	 */
	public void positionReceived(Location location) {
		// TODO: Failswitch einbauen, um Warnung bei zu lange ausbleibenden Positionen anzuzeigen
		if (location.getAccuracy() > minAccuracy) {
			return;
		}

		ui.hideNoPositionOverlay();

		this.currentLocation = location;
		updateDisplay();

		this.locationObserver.fire(location);
	}

	/**
	 * callback for the geolocation
	 */
	public void positionError(Exception error) {
		ui.showNoPositionOverlay();
	}

	/**
	 * diese methode holt sich regelm‰ﬂig (alle 5000ms) ein update from server ob des aktuellen Spielstandes
	 * 
	 * @param isAsync
	 */
	void updateGameStatus(final boolean isAsync) {
		// console.log("updateGameStatus async: "+isAsync);
		if (gameStatusRequestExecutes == false) {
			// console.log("gameStatusRequestExecutes == false");
			gameStatusRequestExecutes = true;

			rest.getGameStatus(playerId, isAsync, new AjaxResult<GameStatus>() {

				@Override
				public void success(GameStatus result) {
					updateGameStatusCallback(result);
				}

				@Override
				public void error(Exception e) {
					gameStatusRequestExecutes = false;
				}
			});
		}
	}

	private void updateGameStatusCallback(GameStatus data) {
		gameStatusRequestExecutes = false;

		int oldRange = playerRange;

		// Spielstatus und Spielinformationen

		gameIsRunning = data.stats.settings.isRunningBoolean();
		remainingPlayingTime = data.stats.getRemainingPlayingTime();
		gameExists = data.stats.isGameExistingBoolean();
		gameDidExist = gameExists;
		itemCollectionRange = data.stats.settings.getItemCollectionRange();
		gameDidEnd = data.stats.hasEndedBoolean();
		Integer updateDisplayIntervalTime = data.stats.settings.getUpdateDisplayIntervalTime();
		intervals.setUpdateDisplayIntervalTime(updateDisplayIntervalTime);

		// Spielerinformationen
		battery = data.node.getBatterieLevel();
		neighbourCount = data.node.getNeighbourCount();
		score = data.node.getScore();
		playerRange = data.node.getRange();
		neighbours = data.node.getNeighbours();
		nearbyItems = data.node.getNearbyItems().getItems();
		nextItemDistance = data.node.getNextItemDistance();
		itemInCollectionRange = data.node.isItemInCollectionRangeBoolean();
		hasRangeBooster = data.node.hasRangeBoosterBoolean();
		hint = data.getHint();

		if (oldRange != playerRange) {
			this.rangeObserver.fire((double) playerRange);
		}

		mapTasks.removeInvisibleMarkers(neighbours, nearbyItems);

		// Spiel entsprechend der erhaltenen Informationen
		// anpassen
		if (gameDidEnd) {
			intervals.stopIntervals();
			ui.showGameEnded();
		} else {
			if (battery > 0) {
				if (!gameExists && gameDidExist) {
					app.reload();
				} else if (!gameExists && !gameDidExist) {
					intervals.restartIntervals(this);
					ui.showWaitingForGameStart();
				} else if (gameExists && gameDidExist && !gameIsRunning) {
					intervals.restartIntervals(this);
					ui.showGamePaused();
				} else {
					intervals.startIntervals(this);
					ui.hideWaitingForGameOverlay();
				}
			} else {
				intervals.stopIntervals();
				ui.showBatteryEmpty();
			}
		}
	}

	/**
	 * updates the display with the new position and the positions of the neighbours
	 */
	void updateDisplay() {
		mapTasks.centerAtCurrentLocation(currentLocation, playerRange, itemCollectionRange);
		mapTasks.drawMarkers(neighbours, nearbyItems);

		ui.updateStatusHeaderAndFooter(score, neighbourCount, remainingPlayingTime, battery, nextItemDistance, hasRangeBooster, itemInCollectionRange, hint);
	}

	/**
	 * collect items
	 */
	public void collectItem() {
		this.pingObserver.fire();
		this.collectObserver.fire(itemCollectionRange);
	}

	private void loginSuccess(LoginAnswer data) {
		if (!isNaN(parseInt(data.id))) {
			playerId = parseInt(data.id);
			ui.hideLoginOverlay();
			updateGameStatus(false);
			intervals.startGameStatusInterval(this);
			// $("#mainContent").html("");

			this.loginObserver.fire(playerId);
		} else {
			ui.showLoginError("Keine id bekommen");
		}
	}

	public static boolean isNaN(double result) {
		return Double.isNaN(result);
	}

	public static double parseFloat(String value) {
		return Double.parseDouble(value);
	}

	public static Integer parseInt(String value) {
		if (value == null) {
			return null;
		}
		return Integer.parseInt(value);
	}
}
