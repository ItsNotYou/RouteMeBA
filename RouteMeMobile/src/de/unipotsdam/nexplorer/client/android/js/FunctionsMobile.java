package de.unipotsdam.nexplorer.client.android.js;

import static de.unipotsdam.nexplorer.client.android.js.Window.app;
import static de.unipotsdam.nexplorer.client.android.js.Window.beginDialog;
import static de.unipotsdam.nexplorer.client.android.js.Window.isNaN;
import static de.unipotsdam.nexplorer.client.android.js.Window.loginButton;
import static de.unipotsdam.nexplorer.client.android.js.Window.loginOverlay;
import static de.unipotsdam.nexplorer.client.android.js.Window.mainPanelToolbar;
import static de.unipotsdam.nexplorer.client.android.js.Window.noPositionOverlay;
import static de.unipotsdam.nexplorer.client.android.js.Window.parseFloat;
import static de.unipotsdam.nexplorer.client.android.js.Window.parseInt;
import static de.unipotsdam.nexplorer.client.android.js.Window.waitingForGameOverlay;
import static de.unipotsdam.nexplorer.client.android.js.Window.waitingText;

import java.util.Date;

import de.unipotsdam.nexplorer.client.android.R;
import de.unipotsdam.nexplorer.client.android.callbacks.AjaxResult;
import de.unipotsdam.nexplorer.client.android.net.RestMobile;
import de.unipotsdam.nexplorer.client.android.support.Location;

/**
 * mainly legacy code from Tobias Moebert has been adapted to work with a java backend and gwt client wrapper
 * 
 * @author Julian Dehne
 */
public class FunctionsMobile implements PositionWatcher {

	MapRelatedTasks mapTasks;
	Intervals intervals;

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

	int playerId;
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

	public FunctionsMobile() {
		this.mapTasks = new MapRelatedTasks();
		this.intervals = new Intervals();
		this.isCollectingItem = false;
	}

	/**
	 * Dise Funktion wird zunächst aufgerufen sie loggt den spier ein und zeigt bei existierenden Spiel eine Karte
	 * 
	 * @param name
	 * @param isMobile
	 */
	public void loginPlayer(final String name, final boolean isMobile) {
		if (name != "") {
			loginButton.label("melde an...");

			new RestMobile().login(name, isMobile, new AjaxResult<LoginAnswer>() {

				@Override
				public void success(LoginAnswer data) {
					loginSuccess(data);
				}

				@Override
				public void error() {
					showLoginError("Exception wurde ausgelößt - Kein Spiel gestartet?");
				}
			});
		}
	}

	private void showLoginError(Object data) {
		beginDialog.setText("Kein Spiel da. Versuchen Sie es später noch einmal!");
		loginButton.label("anmelden ");
	}

	/**
	 * sendet die aktuelle Positionsdaten an den Server
	 * 
	 * @param location
	 */
	void updatePosition(Location location) {
		if (!positionRequestExecutes && location != null) {
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

	/*
	 * // findet in höheren Schwierigkeitsgraden Verwendung private void updateNeighbours() { if (neighboursRequestExecutes == false) { neighboursRequestExecutes = true; $.ajax({ type:"POST", data:"playerId=" + playerId, url:"../php/ajax/mobile/update_neighbours.php", timeout:5000, success:private void () { neighboursRequestExecutes = false; } }) } ; }
	 */

	/**
	 * callback for the geolocation
	 */
	public void positionReceived(Location location) {
		// TODO: Failswitch einbauen, um Warnung bei zu lange ausbleibenden Positionen anzuzeigen
		if (location.getAccuracy() > minAccuracy) {
			return;
		}

		noPositionOverlay.hide();

		this.currentLocation = location;
		updatePosition(currentLocation);
		updateDisplay();
	}

	/**
	 * callback for the geolocation
	 */
	public void positionError(Exception error) {
		noPositionOverlay.show();
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
					showLoginError("Exception wurde ausgelößt - Kein Spiel gestartet?" + e);
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
			waitingText.setText("Das Spiel ist zu Ende. Vielen Dank fürs Mitspielen.");
			intervals.stopIntervals();
			waitingForGameOverlay.show();
		} else {
			if (battery > 0) {
				if (!gameExists && gameDidExist) {
					app.reload();
				} else if (!gameExists && !gameDidExist) {
					waitingText.setText("Warte auf Spielstart");
					intervals.stopIntervals();
					intervals.startGameStatusInterval(this);
					waitingForGameOverlay.show();
				} else if (gameExists && gameDidExist && !gameIsRunning) {
					waitingText.setText("Das Spiel wurde Pausiert");
					intervals.stopIntervals();
					intervals.startGameStatusInterval(this);
					waitingForGameOverlay.show();
				} else {
					// stopIntervals();
					intervals.startIntervals(this);
					waitingForGameOverlay.hide();
				}
			} else {
				waitingText.setText("Dein Akku ist alle :( Vielen Dank fürs Mitspielen.");
				intervals.stopIntervals();
				waitingForGameOverlay.show();
			}
		}

		// Ansicht aktualisieren

		// updateDisplay(); refaktorisiert.... display soll
		// nicht immer nur nach den server calls refreshed
		// werden
	}

	private String addZ(double n) {
		return (n < 10 ? "0" : "") + n;
	}

	/**
	 * 
	 * @param ms
	 * @returns {String}
	 */
	private String convertMS(double s) {
		double ms = s % 1000;
		s = (s - ms) / 1000;
		double secs = s % 60;
		s = (s - secs) / 60;
		double mins = s % 60;

		return addZ(mins);
	}

	/**
	 * updates the display with the new position and the positions of the neighbours
	 */
	void updateDisplay() {
		intervals.ensurePositionWatch(this);
		updateStatusHeader(score, neighbourCount, remainingPlayingTime, battery);

		Window.hint.setText(hint);

		mapTasks.centerAtCurrentLocation(currentLocation, playerRange, itemCollectionRange);

		updateStatusFooter(nextItemDistance, hasRangeBooster, isCollectingItem, itemInCollectionRange);
		mapTasks.drawMarkers(this);
	}

	private void updateStatusFooter(Object nextItemDistance, boolean hasRangeBooster, boolean isCollectingItem, boolean itemInCollectionRange) {
		if (nextItemDistance != null)
			Window.nextItemDistance.setText("Entfernung zum nächsten Gegenstand " + nextItemDistance + " Meter.");
		else
			Window.nextItemDistance.setText("Keine Gegenstände in der Nähe.");

		int boosterImageElement;
		if (hasRangeBooster) {
			boosterImageElement = R.drawable.mobile_phone_cast;
		} else {
			boosterImageElement = R.drawable.mobile_phone_cast_gray;
		}

		Window.activeItems.html("Aktive Gegenstände: ", boosterImageElement);

		if (!isCollectingItem) {
			Window.collectItemButton.html("Gegenstand einsammeln");

			boolean isDisabled = Window.collectItemButton.isDisabled();
			if (itemInCollectionRange && isDisabled) {
				Window.collectItemButton.enable();
			} else if (!itemInCollectionRange && !isDisabled) {
				Window.collectItemButton.disable();
			}
		}
	}

	private void updateStatusHeader(int score, int neighbourCount, int remainingPlayingTime, double battery) {
		if (!isNaN(score))
			mainPanelToolbar.items.getItems()[0].setText(score + "");
		if (!isNaN(neighbourCount))
			mainPanelToolbar.items.getItems()[2].setText(neighbourCount + "");
		if (!isNaN(remainingPlayingTime))
			mainPanelToolbar.items.getItems()[4].setText(convertMS(remainingPlayingTime));
		if (!isNaN(battery))
			mainPanelToolbar.items.getItems()[6].setText((battery + "%").replace(".", ","));
	}

	/**
	 * collect items
	 */
	public void collectItem() {
		if (!isCollectingItem) {
			isCollectingItem = true;

			Window.collectItemButton.disable();
			Window.collectItemButton.html("Gegenstand wird eingesammelt...<img src='media/images/ajax-loader.gif' />");
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
			loginOverlay.hide();
			updateGameStatus(false);
			intervals.startGameStatusInterval(this);
			// $("#mainContent").html("");
		} else {
			showLoginError("Keine id bekommen");
		}
	}
}
