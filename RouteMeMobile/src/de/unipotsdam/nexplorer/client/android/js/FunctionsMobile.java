package de.unipotsdam.nexplorer.client.android.js;

import static de.unipotsdam.nexplorer.client.android.js.Window.app;
import static de.unipotsdam.nexplorer.client.android.js.Window.isNaN;
import static de.unipotsdam.nexplorer.client.android.js.Window.parseFloat;
import static de.unipotsdam.nexplorer.client.android.js.Window.parseInt;

import java.util.Date;

import de.unipotsdam.nexplorer.client.android.callbacks.AjaxResult;
import de.unipotsdam.nexplorer.client.android.net.RestMobile;
import de.unipotsdam.nexplorer.client.android.support.Location;
import de.unipotsdam.nexplorer.client.android.ui.UI;

/**
 * mainly legacy code from Tobias Moebert has been adapted to work with a java backend and gwt client wrapper
 * 
 * @author Julian Dehne
 */
public class FunctionsMobile implements PositionWatcher {

	MapRelatedTasks mapTasks;
	Intervals intervals;
	UI ui;

	// TODO: Parameter flexibilisieren
	double minAccuracy = 11;

	// Interval Ajax Request

	boolean positionRequestExecutes = false;
	boolean gameStatusRequestExecutes = false;
	boolean playerStatusRequestExecutes = false;
	boolean neighboursRequestExecutes = false;

	// Overlays

	// Panels

	// Player data

	Integer playerId = null;
	double serverLatitude;
	double serverLongitude;
	double battery = 100;
	java.util.Map<Integer, Neighbour> neighbours;
	int neighbourCount = 0;
	int score;
	int playerRange;
	java.util.Map<Integer, Item> nearbyItems;
	Object nearbyItemsCount;
	Object nextItemDistance;
	boolean itemInCollectionRange;
	boolean hasRangeBooster;
	String hint = "Achte auf die Hinweise!";

	// Game data

	boolean gameIsRunning;
	boolean gameExists;
	boolean gameDidExist = true; // die semantik davon, dass es mal ein Spiel gegeben
	// hat, ist mir unklar ... es hat hat schon immer ein
	// Spiel gegeben!
	int remainingPlayingTime;
	Object baseNodeRange;
	Object gameDifficulty = 0;
	int itemCollectionRange;
	boolean gameDidEnd = false;

	// Time Tracking

	long updatePositionStartTime;
	long updateGameStatusStartTime;
	Object updatePlayerStatusStartTime;

	long latencyTotal = 0;
	int latencyCount = 0;

	Location currentLocation;
	private boolean isCollectingItem;

	public FunctionsMobile(UI ui) {
		this.mapTasks = new MapRelatedTasks();
		this.intervals = new Intervals();
		this.ui = ui;
		this.isCollectingItem = false;

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

			new RestMobile().login(name, isMobile, new AjaxResult<LoginAnswer>() {

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

			new RestMobile().updatePlayerPosition(playerId, location, new AjaxResult<Object>() {

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
			updateGameStatusStartTime = new Date().getTime();

			new RestMobile().getGameStatus(playerId, isAsync, new AjaxResult<GameStatus>() {

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
		latencyCount++;
		latencyTotal += new Date().getTime() - updateGameStatusStartTime;
		gameStatusRequestExecutes = false;

		// Spielstatus und Spielinformationen

		gameIsRunning = parseInt(data.stats.settings.getIsRunning()) != 0 ? true : false;
		remainingPlayingTime = parseInt(data.stats.getRemainingPlayingTime());
		gameExists = parseInt(data.stats.getGameExists()) != 0 ? true : false;
		gameDidExist = gameExists;
		baseNodeRange = parseInt(data.stats.getBaseNodeRange());
		gameDifficulty = parseInt(data.stats.getGameDifficulty());
		itemCollectionRange = parseInt(data.stats.settings.getItemCollectionRange());
		gameDidEnd = parseInt(data.stats.getDidEnd()) != 0 ? true : false;
		// updatePositionIntervalTime = parseInt(data.stats.settings["updatePositionIntervalTime"]);
		Integer updateDisplayIntervalTime = parseInt(data.stats.settings.getUpdateDisplayIntervalTime());
		intervals.setUpdateDisplayIntervalTime(updateDisplayIntervalTime);

		// Spielerinformationen
		battery = parseFloat(data.node.getBatterieLevel());
		neighbourCount = parseInt(data.node.getNeighbourCount());
		serverLatitude = parseFloat(data.stats.getPlayingFieldCenterLatitude());
		serverLongitude = parseFloat(data.stats.getPlayingFieldCenterLongitude());
		score = parseInt(data.node.getScore());
		playerRange = parseInt(data.node.getRange());
		neighbours = data.node.getNeighbours();
		nearbyItemsCount = parseInt(data.node.getNearbyItemsCount());
		nearbyItems = data.node.getNearbyItems().getItems();
		nextItemDistance = parseInt(data.node.getNextItemDistance());
		itemInCollectionRange = data.node.getItemInCollectionRange() == 0 ? false : true;
		hasRangeBooster = parseInt(data.node.getHasRangeBooster()) != 0 ? true : false;
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
		mapTasks.drawMarkers(this);

		ui.updateStatusHeaderAndFooter(score, neighbourCount, remainingPlayingTime, battery, nextItemDistance, hasRangeBooster, isCollectingItem, itemInCollectionRange, hint);
	}

	/**
	 * collect items
	 */
	public void collectItem() {
		if (!isCollectingItem) {
			isCollectingItem = true;

			ui.disableButtonForItemCollection();
			new RestMobile().collectItem(playerId, new AjaxResult<Object>() {

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
}
