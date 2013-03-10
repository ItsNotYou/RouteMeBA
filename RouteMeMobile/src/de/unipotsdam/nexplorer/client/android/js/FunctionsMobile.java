package de.unipotsdam.nexplorer.client.android.js;

import static de.unipotsdam.nexplorer.client.android.js.Window.ajax;
import static de.unipotsdam.nexplorer.client.android.js.Window.beginDialog;
import static de.unipotsdam.nexplorer.client.android.js.Window.clearInterval;
import static de.unipotsdam.nexplorer.client.android.js.Window.each;
import static de.unipotsdam.nexplorer.client.android.js.Window.isNaN;
import static de.unipotsdam.nexplorer.client.android.js.Window.location;
import static de.unipotsdam.nexplorer.client.android.js.Window.loginButton;
import static de.unipotsdam.nexplorer.client.android.js.Window.parseFloat;
import static de.unipotsdam.nexplorer.client.android.js.Window.setInterval;
import static de.unipotsdam.nexplorer.client.android.js.Window.undefined;
import static de.unipotsdam.nexplorer.client.android.js.Window.waitingText;
import static java.lang.Integer.parseInt;

import java.util.Date;
import java.util.HashMap;

import de.unipotsdam.nexplorer.client.android.support.Location;

/**
 * mainly legacy code from Tobias Moebert has been adapted to work with a java backend and gwt client wrapper
 * 
 * @author Julian Dehne
 */
public class FunctionsMobile implements PositionWatcher {

	Navigator navigator = new Navigator();

	SenchaMap senchaMap;
	Object playerMaker;
	PlayerRadius playerRadius;
	PlayerRadius collectionRadius;
	// TODO: Parameter flexibilisieren
	double minAccuracy = 11;

	java.util.Map<Integer, Marker> neighbourMarkersArray = new HashMap();
	java.util.Map<Integer, Marker> nearbyItemMarkersArray = new HashMap();

	// Intervals

	Interval localisationInterval;
	Interval gameStatusInterval;
	Interval displayMarkerInterval;

	// Interval Ajax Request

	boolean positionRequestExecutes = false;
	boolean gameStatusRequestExecutes = false;
	boolean playerStatusRequestExecutes = false;
	boolean neighboursRequestExecutes = false;

	// Interval Times

	long updatePositionIntervalTime = 300;
	long updateDisplayIntervalTime = 300;

	Object positionWatch = null;

	// Toolbars

	MainPanelToolbar mainPanelToolbar;

	// Overlays

	LoginOverlay loginOverlay;
	WaitingForGameOverlay waitingForGameOverlay;
	NoPositionOverlay noPositionOverlay;

	// Panels

	Object mainPanel;

	// Player data

	Object playerName;
	Object playerId;
	Object serverLatitude;
	Object serverLongitude;
	Double gpsLatitude; // fixed error with gps latitude
	Double gpsLongitude;
	Object gpsAccuracy;
	Object gpsSpeed;
	Object gpsHeading;
	double battery = 100;
	java.util.Map<Integer, Neighbour> neighbours;
	int neighbourCount = 0;
	int score;
	Object playerRange;
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
	Object itemCollectionRange;
	boolean gameDidEnd = false;

	// Time Tracking

	long updatePositionStartTime;
	long updateGameStatusStartTime;
	Object updatePlayerStatusStartTime;

	long latencyTotal = 0;
	int latencyCount = 0;

	Marker playerMarker;

	// TODO Port layout
	// Ext
	// .setup({
	// icon : 'icon.png',
	// glossOnIcon : false,
	// tabletStartupScreen : 'tablet_startup.png',
	// phoneStartupScreen : 'phone_startup.png',
	// onReady : private void() {
	// Object loginOverlayToolbar = new Ext.Toolbar({
	// dock : 'top',
	// title : 'Anmeldung'
	// });
	//
	// loginOverlay = new Ext.Panel({
	// floating : true,
	// modal : true,
	// centered : true,
	// width : 300,
	// height : 300,
	// styleHtmlContent : true,
	// dockedItems : loginOverlayToolbar,
	// hideOnMaskTap : false,
	// scroll : 'vertical',
	// contentEl : 'login',
	// cls : 'htmlcontent'
	// });
	//
	// waitingForGameOverlay = new Ext.Panel({
	// floating : true,
	// modal : true,
	// centered : true,
	// width : 150,
	// height : 150,
	// styleHtmlContent : true,
	// hideOnMaskTap : false,
	// scroll : 'vertical',
	// contentEl : 'waiting',
	// cls : 'htmlcontent'
	// });
	//
	// noPositionOverlay = new Ext.Panel({
	// floating : true,
	// modal : true,
	// centered : true,
	// width : 150,
	// height : 150,
	// styleHtmlContent : true,
	// hideOnMaskTap : false,
	// scroll : 'vertical',
	// contentEl : 'noPosition',
	// cls : 'htmlcontent'
	// });
	//
	// Object showLoginOverlay = private void(btn, event) {
	// loginOverlay.show();
	// };
	//
	// Object hideLoginOverlay = private void(btn, event) {
	// loginOverlay.hide();
	// }
	//
	// mainPanelToolbar = new Ext.Toolbar({
	// dock : 'top',
	// items : [ {
	// iconMask : false,
	// ui : 'plain',
	// text : "0",
	// icon : "media/images/icons/point.png",
	// disabled : true,
	// disabledCls : ''
	// }, {
	// xtype : 'spacer'
	// }, {
	// iconMask : false,
	// ui : 'plain',
	// text : "0",
	// icon : "media/images/icons/neighborhood.png",
	// disabled : true,
	// disabledCls : ''
	// }, {
	// xtype : 'spacer'
	// }, {
	// iconMask : false,
	// ui : 'plain',
	// text : "0",
	// icon : "media/images/icons/clock.png",
	// disabled : true,
	// disabledCls : ''
	// }, {
	// xtype : 'spacer'
	// }, {
	// iconMask : false,
	// ui : 'plain',
	// text : "0%",
	// icon : "media/images/icons/battery-full.png",
	// disabled : true,
	// disabledCls : ''
	// } ]
	// });
	//
	// Object image = new google.maps.MarkerImage(
	// 'media/images/icons/home-network.png',
	// new google.maps.Size(16, 16), new google.maps.Point(0,
	// 0), new google.maps.Point(8, 8));
	//
	// Object position = new google.maps.LatLng(37.44885, -122.158592)
	//
	// senchaMap = new Ext.Map({
	// useCurrentLocation : false,
	// mapOptions : {
	// zoom : 19,
	// mapTypeId : google.maps.MapTypeId.ROADMAP,
	// navigationControl : false,
	// disableDefaultUI : true,
	// disableDoubleClickZoom : true,
	// draggable : false,
	// scrollwheel : false
	// }
	// });
	//
	// playerMarker = new google.maps.Marker({
	// title : 'My Current Location',
	// icon : image,
	// zIndex : 1
	// })
	//
	// playerRadius = new google.maps.Circle({
	// strokeColor : "#0000FF",
	// strokeOpacity : 0.35,
	// strokeWeight : 2,
	// fillColor : "#0000FF",
	// fillOpacity : 0.20
	// });
	//
	// collectionRadius = new google.maps.Circle({
	// strokeColor : "#FF0000",
	// strokeOpacity : 0.35,
	// strokeWeight : 1,
	// fillColor : "#FF0000",
	// fillOpacity : 0.25
	// });
	//
	// Object mainContent = {
	// style : "width:100%; background-color:#C9DAEF; font-size:12px; text-align: center; padding: 3px",
	// title : "mainContent",
	// id : "mainContent",
	// html : "<div id='nextItemDistance' style='width:100%'></div><div id='activeItems' style='width:100%'></div><div style='width:100%'><button id='collectItemButton' type='button' disabled='disabled' onclick='collectItem()' />Gegenstand einsammeln</button></div><div class='ui-state-highlight ui-corner-all' style='width:100%'><span class='ui-icon ui-icon-info' style='float: left; margin-right: .3em;'></span><span id='hint'></span></div>",
	// flex : 0.5
	// };
	//
	// mainPanel = new Ext.Panel({
	// fullscreen : true,
	// dockedItems : [ mainPanelToolbar ],
	// items : [ senchaMap, mainContent ],
	// layoutOnOrientationChange : false,
	// layout : {
	// type : "vbox",
	// align : "stretch",
	// pack : "center"
	// },
	// defaults : {
	// flex : 1
	// }
	// })
	//
	// showLoginOverlay();
	// $("button").button();
	// }
	// });

	/**
	 * Dise Funktion wird zun�chst aufgerufen sie loggt den spier ein und zeigt bei existierenden Spiel eine Karte
	 * 
	 * @param name
	 * @param isMobile
	 */
	private void loginPlayer(final String name, final boolean isMobile) {
		if (name != "") {
			loginButton.label("melde an...");

			ajax(new Options<LoginAnswer>() {

				String type = "POST";
				String url = "../rest/loginManager/login_player_mobile";
				String data = "name=" + name + "&isMobile=" + isMobile;

				public void success(LoginAnswer data) {
					if (!isNaN(parseInt(data.id))) {
						playerId = parseInt(data.id);
						loginOverlay.hide();
						updateGameStatus(false);
						startGameStatusInterval();
						// $("#mainContent").html("");
					} else {
						showLoginError("Keine id bekommen");
					}
				}

				public void error() {
					showLoginError("Exception wurde ausgel��t - Kein Spiel gestartet?");
				}
			});
		}
	}

	private void showLoginError(Object data) {
		beginDialog.html("Kein Spiel da. Versuchen Sie es sp�ter noch einmal!");
		loginButton.label("anmelden ");
	}

	/**
	 * bewirkt, dass das Display regelm��ig aktualisiert wird und die aktuelle Position an den Server gesendet wird
	 */
	private void startIntervals() {
		startGameStatusInterval();
		startLocalisationInterval();
		startDisplayInterval();
	}

	private void stopIntervals() {
		clearInterval(localisationInterval);
		localisationInterval = null;

		navigator.geolocation.clearWatch(positionWatch);
		positionWatch = null;
	}

	private void startGameStatusInterval() {
		if (gameStatusInterval == undefined || gameStatusInterval == null) {
			gameStatusInterval = setInterval("updateGameStatus(true)", updateDisplayIntervalTime);
		}
	}

	private void startLocalisationInterval() {
		if (localisationInterval == undefined || localisationInterval == null) {
			localisationInterval = setInterval("updatePosition()", updatePositionIntervalTime);
		}
	}

	private void startDisplayInterval() {
		if (displayMarkerInterval == undefined || displayMarkerInterval == null) {
			displayMarkerInterval = setInterval("updateDisplay()", 1);
		}
	}

	/**
	 * sendet die aktuelle Positionsdaten an den Server
	 */
	private void updatePosition() {
		if (positionRequestExecutes == false && gpsLatitude != undefined && gpsLongitude != undefined) {
			positionRequestExecutes = true;
			updatePositionStartTime = new Date().getTime();
			ajax(new Options() {

				String type = "POST";
				String url = "../rest/mobile/update_player_position";
				String data = "latitude=" + gpsLatitude + "&longitude=" + gpsLongitude + "&accuracy=" + gpsAccuracy + "&playerId=" + playerId + "&speed=" + gpsSpeed + "&heading=" + gpsHeading;
				long timeout = 5000;

				public void success(Object result) {
					latencyCount++;
					latencyTotal += new Date().getTime() - updatePositionStartTime;
					// console.log("Count: " + latencyCount + " Latenz: " +
					// (latencyTotal / latencyCount));
					positionRequestExecutes = false;
				}
			});
		}
		;
	}

	/*
	 * // findet in h�heren Schwierigkeitsgraden Verwendung private void updateNeighbours() { if (neighboursRequestExecutes == false) { neighboursRequestExecutes = true; $.ajax({ type:"POST", data:"playerId=" + playerId, url:"../php/ajax/mobile/update_neighbours.php", timeout:5000, success:private void () { neighboursRequestExecutes = false; } }) } ; }
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
		gpsLatitude = location.getLatitude();
		gpsLongitude = location.getLongitude();
		gpsAccuracy = location.getAccuracy();
		// TODO Port
		// gpsSpeed = location.getSpeed();
		// gpsHeading = location.getHeading();
	}

	/**
	 * callback for the geolocation
	 */
	public void positionError(Exception error) {
		noPositionOverlay.show();
	}

	/**
	 * diese methode holt sich regelm��ig (alle 5000ms) ein update from server ob des aktuellen Spielstandes
	 * 
	 * @param isAsync
	 */
	private void updateGameStatus(final boolean isAsync) {
		// console.log("updateGameStatus async: "+isAsync);
		if (gameStatusRequestExecutes == false) {
			// console.log("gameStatusRequestExecutes == false");
			gameStatusRequestExecutes = true;
			updateGameStatusStartTime = new Date().getTime();
			ajax(new Options<GameStatus>() {

				String dataType = "json";
				String url = "../rest/mobile/get_game_status";
				boolean async = isAsync;
				String data = "playerId=" + playerId;
				long timeout = 5000;

				public void success(GameStatus data) {
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
					updateDisplayIntervalTime = parseInt(data.stats.settings.getUpdateDisplayIntervalTime());

					// Spielerinformationen
					battery = parseFloat(data.node.getBatterieLevel());
					neighbourCount = parseInt(data.node.getNeighbourCount());
					serverLatitude = parseFloat(data.stats.getPlayingFieldCenterLatitude());
					serverLongitude = parseFloat(data.stats.getPlayingFieldCenterLongitude());
					score = parseInt(data.node.getScore());
					playerRange = parseInt(data.node.getRange());
					neighbours = data.node.getNeighbours();
					nearbyItemsCount = parseInt(data.node.getNearbyItemsCount());
					nearbyItems = data.node.getNearbyItems();
					// TODO Check formate
					// nearbyItems = nearbyItems.items;
					nextItemDistance = parseInt(data.node.getNextItemDistance());
					itemInCollectionRange = data.node.getItemInCollectionRange();
					hasRangeBooster = parseInt(data.node.getHasRangeBooster()) != 0 ? true : false;
					hint = data.getHint();

					each(neighbourMarkersArray, new Call() {

						public void call(int key, Object theMarker) {
							if (theMarker != undefined && neighbours.get(key) == undefined) {
								neighbourMarkersArray.get(key).setMap(null);
							}
						}
					});

					each(nearbyItemMarkersArray, new Call() {

						public void call(int key, Object theMarker) {
							if (theMarker != undefined && nearbyItems.get(key) == undefined) {
								nearbyItemMarkersArray.get(key).setMap(null);
							}
						}
					});

					// Spiel entsprechend der erhaltenen Informationen
					// anpassen
					if (gameDidEnd) {
						waitingText.html("Das Spiel ist zu Ende. Vielen Dank f�rs Mitspielen.");
						stopIntervals();
						waitingForGameOverlay.show();
					} else {
						if (battery > 0) {
							if (!gameExists && gameDidExist) {
								location.reload();
							} else if (!gameExists && !gameDidExist) {
								waitingText.html("Warte auf Spielstart");
								stopIntervals();
								startGameStatusInterval();
								waitingForGameOverlay.show();
							} else if (gameExists && gameDidExist && !gameIsRunning) {
								waitingText.html("Das Spiel wurde Pausiert");
								stopIntervals();
								startGameStatusInterval();
								waitingForGameOverlay.show();
							} else {
								stopIntervals();
								startIntervals();
								waitingForGameOverlay.hide();
							}
						} else {
							waitingText.html("Dein Akku ist alle :( Vielen Dank f�rs Mitspielen.");
							stopIntervals();
							waitingForGameOverlay.show();
						}
					}

					// Ansicht aktualisieren

					// updateDisplay(); refaktorisiert.... display soll
					// nicht immer nur nach den server calls refreshed
					// werden
				}

				public void error(Exception data) {
					gameStatusRequestExecutes = false;
					showLoginError("Exception wurde ausgel��t - Kein Spiel gestartet?" + data);
				}
			});
		}
		;
	}

	/**
	 * draw the neighbours
	 * 
	 * @param playerId
	 * @param latitude
	 * @param longitude
	 */
	private void drawNeighbourMarkerAtLatitudeLongitude(final int playerId, double latitude, double longitude) {
		final LatLng latlng = new LatLng(latitude, longitude);

		final MarkerImage image = new MarkerImage("media/images/icons/network-wireless-small.png", new Size(16, 16),
		// The origin for this image is 0,0.
				new Point(0, 0),
				// The anchor for this image is the base of the flagpole at 0,32.
				new Point(8, 8));

		if (neighbourMarkersArray.get(playerId) == undefined) {
			Marker marker = new Marker() {

				LatLng position = latlng;
				Map map = senchaMap.map;
				String title = "(" + playerId + ") " /* + name */;
				MarkerImage icon = image;
				int zIndex = 1;
			};

			neighbourMarkersArray.put(playerId, marker);
		} else {
			neighbourMarkersArray.get(playerId).setPosition(latlng);
			neighbourMarkersArray.get(playerId).setTitle("(" + playerId + ") " /* + name */);
			if (neighbourMarkersArray.get(playerId).map == null) {
				neighbourMarkersArray.get(playerId).setMap(senchaMap.map);
			}
			;
		}
	}

	/**
	 * draw nearby items
	 * 
	 * @param itemId
	 * @param type
	 * @param latitude
	 * @param longitude
	 */
	private void drawNearbyItemMarkerAtLatitudeLongitude(int itemId, String type, double latitude, double longitude) {
		final LatLng latlng = new LatLng(latitude, longitude);

		String imagePath = null;
		if (type == "BATTERY") {
			imagePath = "media/images/icons/battery-charge.png";
		} else {
			imagePath = "media/images/icons/mobile-phone-cast.png";
		}

		final MarkerImage image = new MarkerImage(imagePath, new Size(16, 16),
		// The origin for this image is 0,0.
				new Point(0, 0),
				// The anchor for this image is the base of the flagpole at 0,32.
				new Point(8, 8));

		if (nearbyItemMarkersArray.get(itemId) == undefined) {
			Marker marker = new Marker() {

				LatLng position = latlng;
				Map map = senchaMap.map;
				MarkerImage icon = image;
				int zIndex = 1;
			};

			nearbyItemMarkersArray.put(itemId, marker);
		} else {
			nearbyItemMarkersArray.get(itemId).setPosition(latlng);
			if (nearbyItemMarkersArray.get(itemId).map == null) {
				nearbyItemMarkersArray.get(itemId).setMap(senchaMap.map);
			}
			;
		}
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
		double hrs = (s - mins) / 60;

		return addZ(mins);
	}

	/**
	 * updates the display with the new position and the positions of the neighbours
	 */
	private void updateDisplay() {
		// console.log("updateDisplay");
		if (positionWatch == null) {
			positionWatch = navigator.geolocation.watchPosition(this, new NavigatorOptions() {

				boolean enableHighAccuracy = true;
				int maximumAge = 0;
				int timeout = 9000;
			});
		}
		if (!isNaN(score))
			mainPanelToolbar.items.getItems()[0].setText(score + "");
		if (!isNaN(neighbourCount))
			mainPanelToolbar.items.getItems()[2].setText(neighbourCount + "");
		if (!isNaN(remainingPlayingTime))
			mainPanelToolbar.items.getItems()[4].setText(convertMS(remainingPlayingTime));
		if (!isNaN(battery))
			mainPanelToolbar.items.getItems()[6].setText((battery + "%").replace(".", ","));

		Window.hint.html(hint);

		if (gpsLatitude != null && gpsLongitude != null) {
			// Karte zentrieren
			senchaMap.map.setCenter(new LatLng(gpsLatitude, gpsLongitude));
			// Spieler Marker zentrieren
			playerMarker.setPosition(new LatLng(gpsLatitude, gpsLongitude));
			if (playerMarker.map == null) {
				playerMarker.setMap(senchaMap.map);
			}
			// Senderadius zentrieren
			playerRadius.setCenter(new LatLng(gpsLatitude, gpsLongitude));
			if (playerRadius.map == null) {
				playerRadius.setMap(senchaMap.map);
			}
			playerRadius.setRadius(playerRange);
			// Sammelradius zentrieren
			collectionRadius.setCenter(new LatLng(gpsLatitude, gpsLongitude));
			if (collectionRadius.map == null) {
				collectionRadius.setMap(senchaMap.map);
			}
			collectionRadius.setRadius(itemCollectionRange);
		}

		if (nextItemDistance != null)
			Window.nextItemDistance.html("Entfernung zum n�chsten Gegenstand " + nextItemDistance + " Meter.");
		else
			Window.nextItemDistance.html("Keine Gegenst�nde in der N�he.");

		String boosterImageElement = null;
		if (hasRangeBooster) {
			boosterImageElement = "<img src='media/images/icons/mobile-phone-cast.png' />";
		} else {
			boosterImageElement = "<img src='media/images/icons/mobile-phone-cast-gray.png' />";
		}

		Window.activeItems.html("Aktive Gegenst�nde: " + boosterImageElement);

		boolean isDisabled = Window.collectItemButton.isDisabled();
		if (itemInCollectionRange && isDisabled) {
			Window.collectItemButton.enable();
		} else if (!itemInCollectionRange && !isDisabled) {
			Window.collectItemButton.disable();
		}

		if (neighbours != undefined) {
			each(neighbours, new Call<Neighbour>() {

				@Override
				public void call(int key, Neighbour value) {
					drawNeighbourMarkerAtLatitudeLongitude(key, value.getLatitude(), value.getLongitude());
				}
			});
		}

		if (nearbyItems != undefined) {
			each(nearbyItems, new Call<Item>() {

				@Override
				public void call(int key, Item value) {
					drawNearbyItemMarkerAtLatitudeLongitude(key, value.getItemType(), value.getLatitude(), value.getLongitude());
				}
			});
		}
	}

	/**
	 * collect items
	 */
	private void collectItem() {
		Window.collectItemButton.disable();
		Window.collectItemButton.html("Gegenstand wird eingesammelt...<img src='media/images/ajax-loader.gif' />");
		ajax(new Options() {

			String type = "POST";
			String url = "../rest/mobile/collect_item";
			String data = "playerId=" + playerId;

			public void success() {
				updateDisplay();
			}
		});
	}
}
