package de.unipotsdam.nexplorer.client.android.js;

import de.unipotsdam.nexplorer.client.android.callbacks.AjaxResult;
import de.unipotsdam.nexplorer.client.android.net.RestMobile;
import de.unipotsdam.nexplorer.client.android.rest.GameStatus;
import de.unipotsdam.nexplorer.client.android.rest.Item;
import de.unipotsdam.nexplorer.client.android.rest.LoginAnswer;
import de.unipotsdam.nexplorer.client.android.rest.Neighbour;
import de.unipotsdam.nexplorer.client.android.support.Location;
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

	private boolean positionRequestExecutes = false;
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
	private boolean isCollectingItem;
	private RestMobile rest;
	private RadiusBlinker radiusBlinker;

	public FunctionsMobile(UI ui, AppWrapper app, Intervals intervals, MapRelatedTasks mapTasks, RestMobile rest, RadiusBlinker blinker) {
		this.mapTasks = mapTasks;
		this.intervals = intervals;
		this.app = app;
		this.ui = ui;
		this.rest = rest;
		this.isCollectingItem = false;
		this.radiusBlinker = blinker;

		intervals.ensurePositionWatch(this);
	}

	/**
	 * Dise Funktion wird zunächst aufgerufen sie loggt den spier ein und zeigt bei existierenden Spiel eine Karte
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
					ui.showLoginError("Exception wurde ausgelößt - Kein Spiel gestartet?");
				}
			});
		}
	}

	/**
	 * sendet die aktuelle Positionsdaten an den Server
	 * 
	 * @param location
	 */
	private void updatePosition(Location location) {
		if (!positionRequestExecutes && location != null && playerId != null) {
			positionRequestExecutes = true;

			rest.updatePlayerPosition(playerId, location, new AjaxResult<Object>() {

				@Override
				public void success() {
					positionRequestExecutes = false;
				}

				@Override
				public void error() {
					positionRequestExecutes = false;
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
		updatePosition(currentLocation);
		updateDisplay();
	}

	/**
	 * callback for the geolocation
	 */
	public void positionError(Exception error) {
		ui.showNoPositionOverlay();
	}

	/**
	 * diese methode holt sich regelmäßig (alle 5000ms) ein update from server ob des aktuellen Spielstandes
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

		ui.updateStatusHeaderAndFooter(score, neighbourCount, remainingPlayingTime, battery, nextItemDistance, hasRangeBooster, isCollectingItem, itemInCollectionRange, hint);
	}

	/**
	 * collect items
	 */
	public void collectItem() {
		radiusBlinker.start(new LatLng(currentLocation));

		if (!isCollectingItem) {
			isCollectingItem = true;

			ui.disableButtonForItemCollection();
			rest.collectItem(playerId, new AjaxResult<Object>() {

				@Override
				public void success() {
					isCollectingItem = false;
					updateDisplay();
				}

				@Override
				public void error() {
					isCollectingItem = false;
					updateDisplay();
				}
			});
		}
	}

	private void loginSuccess(LoginAnswer data) {
		if (!isNaN(parseInt(data.id))) {
			playerId = parseInt(data.id);
			ui.hideLoginOverlay();
			updateGameStatus(false);
			intervals.startGameStatusInterval(this);
			// $("#mainContent").html("");
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
