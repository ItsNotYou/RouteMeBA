// Google Map

var senchaMap;
var playerMaker;
var playerRadius;
var collectionRadius;

var neighbourMarkersArray = [];
var nearbyItemMarkersArray = [];

// Intervals

var localisationInterval;
var gameStatusInterval;

// Interval Ajax Request

var positionRequestExecutes = false;
var gameStatusRequestExecutes = false;
var playerStatusRequestExecutes = false;
var neighboursRequestExecutes = false;

// Interval Times

var updatePositionIntervalTime = 3000;
var updateDisplayIntervalTime = 3000;

var positionWatch = null;

// Toolbars

var mainPanelToolbar;

// Overlays

var loginOverlay;
var waitingForGameOverlay;
var noPositionOverlay;

// Panels

var mainPanel;

// Player data

var playerName;
var playerId;
var serverLatitude;
var gpsLongitude;
var serverLongitude;
var gpsLongitude;
var battery = 100;
var neighbours;
var neighbourCount = 0;
var score;
var playerRange;
var nearbyItems;
var nearbyItemsCount;
var nextItemDistance;
var itemInCollectionRange;
var hasRangeBooster;
var hint = "Achte auf die Hinweise!";

// Game data

var gameIsRunning;
var gameExists;
var gameDidExist;
var remainingPlayingTime;
var baseNodeRange;
var gameDifficulty = 0;
var itemCollectionRange;
var gameDidEnd = 0;

// Time Tracking

var updatePositionStartTime;
var updateGameStatusStartTime;
var updatePlayerStatusStartTime;

var latencyTotal = 0;
var latencyCount = 0;

Ext.setup({
    icon: 'icon.png',
    glossOnIcon: false,
    tabletStartupScreen: 'tablet_startup.png',
    phoneStartupScreen: 'phone_startup.png',
    onReady: function() {
        var loginOverlayToolbar = new Ext.Toolbar({
            dock: 'top',
			title: 'Anmeldung'
        });

        loginOverlay = new Ext.Panel({
            floating: true,
            modal: true,
            centered: true,
            width: 300,
            height: 300,
            styleHtmlContent: true,
            dockedItems: loginOverlayToolbar,
			hideOnMaskTap: false,
            scroll: 'vertical',
            contentEl: 'login',
            cls: 'htmlcontent'
        });

		waitingForGameOverlay = new Ext.Panel({
            floating: true,
            modal: true,
            centered: true,
            width: 150,
            height: 150,
            styleHtmlContent: true,
			hideOnMaskTap: false,
            scroll: 'vertical',
            contentEl: 'waiting',
            cls: 'htmlcontent'
        });

		noPositionOverlay = new Ext.Panel({
            floating: true,
            modal: true,
            centered: true,
            width: 150,
            height: 150,
            styleHtmlContent: true,
			hideOnMaskTap: false,
            scroll: 'vertical',
            contentEl: 'noPosition',
            cls: 'htmlcontent'
        });

        var showLoginOverlay = function(btn, event) {
            loginOverlay.show();
        };

		var hideLoginOverlay = function(btn, event) {
			loginOverlay.hide();
		}

		mainPanelToolbar = new Ext.Toolbar({
		    dock : 'top',
		    items: [
                { iconMask: false, ui: 'plain', text: "0", icon: "../media/images/icons/point.png", disabled: true , disabledCls: ''},
				{ xtype: 'spacer' },
				{ iconMask: false, ui: 'plain', text: "0", icon: "../media/images/icons/neighborhood.png", disabled: true , disabledCls: ''},
				{ xtype: 'spacer' },
				{ iconMask: false, ui: 'plain', text: "0", icon: "../media/images/icons/clock.png", disabled: true , disabledCls: ''},
				{ xtype: 'spacer' },
				{ iconMask: false, ui: 'plain', text: "0%", icon: "../media/images/icons/battery-full.png", disabled: true , disabledCls: '' }	
			]
		});
		
		var image = new google.maps.MarkerImage(
            '../media/images/icons/home-network.png',
            new google.maps.Size(16, 16),
            new google.maps.Point(0,0),
            new google.maps.Point(8, 8)
		);
		
		var position = new google.maps.LatLng(37.44885,-122.158592)
		
		senchaMap = new Ext.Map({
			useCurrentLocation: false,
            mapOptions : {
                zoom : 19,
                mapTypeId : google.maps.MapTypeId.ROADMAP,
                navigationControl: false,
				disableDefaultUI: true,
			  	disableDoubleClickZoom: true,
				draggable: false,
				scrollwheel: false
            }
        });

		playerMarker = new google.maps.Marker({
            title : 'My Current Location',
            icon : image,
			zIndex: 1
        })

		playerRadius = new google.maps.Circle({
	        strokeColor: "#0000FF",
	        strokeOpacity: 0.35,
	        strokeWeight: 2,
	        fillColor: "#0000FF",
	        fillOpacity: 0.20
	    });
	
		collectionRadius = new google.maps.Circle({
	        strokeColor: "#FF0000",
	        strokeOpacity: 0.35,
	        strokeWeight: 1,
	        fillColor: "#FF0000",
	        fillOpacity: 0.25
	    });

		var mainContent = {
			style: "width:100%; background-color:#C9DAEF; font-size:12px; text-align: center; padding: 3px",
			title: "mainContent", id: "mainContent", html: "<div id='nextItemDistance' style='width:100%'></div><div id='activeItems' style='width:100%'></div><div style='width:100%'><button id='collectItemButton' type='button' disabled='disabled' onclick='collectItem()' />Gegenstand einsammeln</button></div><div class='ui-state-highlight ui-corner-all' style='width:100%'><span class='ui-icon ui-icon-info' style='float: left; margin-right: .3em;'></span><span id='hint'></span></div>",
			flex: 0.5
		};

		mainPanel = new Ext.Panel({
            fullscreen: true,
            dockedItems: [mainPanelToolbar],
			items: [senchaMap, mainContent],
			layoutOnOrientationChange: false,
			layout: {
				type: "vbox",
				align: "stretch",
				pack: "center"
			},
			defaults: {flex: 1},
        })

		showLoginOverlay();
		$("button").button();
	}
});

function loginPlayer(name, isMobile) {
	if (name != "") {
		$("#loginButton").button({ label: "melde an..." });
		$("#loginButton").attr('disabled', 'disabled');
		$.ajax({
			type: "POST",
			url: "../php/ajax/login_player.php",
			data: "name="+name+"&isMobile="+isMobile,
			success: function(data) {
				if (!isNaN(parseInt(data))) {
					playerId = parseInt(data);
					loginOverlay.hide();
					updateGameStatus(false);
					startGameStatusInterval();
				} else {
					$("#mainContent").html("Anmeldung fehlgeschlagen! <br/><br/>" + data);
				}
			}
		});
	}
}

function startIntervals() {
	startLocalisationInterval();
	startGameStatusInterval();
	
	if (positionWatch == null) {
		positionWatch = navigator.geolocation.watchPosition(positionReceived, positionError, {enableHighAccuracy: true, maximumAge:0, timeout:9000});
	};
}

function stopIntervals() {
	clearInterval(localisationInterval);
	localisationInterval = null;
	
	navigator.geolocation.clearWatch(positionWatch);
	positionWatch = null;
}

function startGameStatusInterval() {
	if (gameStatusInterval === undefined || gameStatusInterval === null) { gameStatusInterval = window.setInterval("updateGameStatus(true)", updateDisplayIntervalTime); }
}

function startLocalisationInterval() {
	if (localisationInterval === undefined || localisationInterval === null) { localisationInterval = window.setInterval("updatePosition()", updatePositionIntervalTime); }
}

function updatePosition() {
	if (positionRequestExecutes === false) {
		positionRequestExecutes = true;
		updatePositionStartTime = new Date().getTime();
		$.ajax({
			type: "POST",
			url: "../php/ajax/mobile/update_player_position.php",
			data: "latitude="+gpsLatitude+"&longitude="+gpsLongitude+"&playerId="+playerId,
			timeout: 5000,
			success: function() {
				latencyCount++;
				latencyTotal += new Date().getTime() - updatePositionStartTime;
				console.log("Count: "+latencyCount+" Latenz: "+(latencyTotal/latencyCount));
				positionRequestExecutes = false;
			}
		});
	};	
}

// findet in höheren Schwierigkeitsgraden Verwendung
function updateNeighbours() {
	if (neighboursRequestExecutes === false) {
		neighboursRequestExecutes = true;
		$.ajax({
			type: "POST",
			data: "playerId="+playerId,
			url: "../php/ajax/mobile/update_neighbours.php",
			timeout: 5000,
			success: function() {
				neighboursRequestExecutes = false;
			}
		})
	};
}

function positionReceived(location) {	
	noPositionOverlay.hide();
	
	gpsLatitude = location.coords.latitude;
	gpsLongitude = location.coords.longitude;
}

function positionError(error) {
	noPositionOverlay.show();
}

function updateGameStatus(isAsync) {
	//console.log("updateGameStatus async: "+isAsync);
	if (gameStatusRequestExecutes === false) {
		//console.log("gameStatusRequestExecutes === false");
		gameStatusRequestExecutes = true;
		updateGameStatusStartTime = new Date().getTime();
		$.ajax({
			dataType: "json",
			url: "../php/json/mobile/get_game_status.php",
			async: isAsync,
			data: "playerId="+playerId,
			timeout: 5000,
			success: function(data) {
				latencyCount++;
				latencyTotal += new Date().getTime() - updateGameStatusStartTime;
				gameStatusRequestExecutes = false;
				
				// Spielstatus und Spielinformationen
				
				gameDidExist = gameExists;
				gameIsRunning = parseInt(data["isRunning"]);
				remainingPlayingTime = parseInt(data["remainingPlayingTime"]);
				gameExists = parseInt(data["gameExists"]);
				baseNodeRange = parseInt(data["baseNodeRange"]);
				gameDifficulty = parseInt(data["gameDifficulty"]);
				itemCollectionRange = parseInt(data["itemCollectionRange"]);
				gameDidEnd = parseInt(data["didEnd"]);
				updatePositionIntervalTime = parseInt(data["updatePositionIntervalTime"]);
				updateDisplayIntervalTime = parseInt(data["updateDisplayIntervalTime"]);

				// Spielerinformationen

				battery = parseFloat(data["battery"]);
				neighbourCount = parseInt(data["neighbourCount"]);
				serverLatitude = parseFloat(data["latitude"]);
				serverLongitude = parseFloat(data["longitude"]);
				score = parseInt(data["score"]);
				playerRange = parseInt(data["range"]);
				neighbours = data["neighbours"];
				nearbyItemsCount = parseInt(data["nearbyItemsCount"]);
				nearbyItems = data["nearbyItems"];
				nextItemDistance = parseInt(data["nextItemDistance"]);
				itemInCollectionRange = parseInt(data["itemInCollectionRange"]);
				hasRangeBooster = parseInt(data["hasRangeBooster"]);
				hint = data["hint"];

				$.each(neighbourMarkersArray, function(key, theMarker) {
					if (theMarker != undefined && data["neighbours"][key] == undefined) {
						neighbourMarkersArray[key].setMap(null);
					}
				});

				$.each(nearbyItemMarkersArray, function(key, theMarker) {
					if (theMarker != undefined && data["nearbyItems"][key] == undefined) {
						nearbyItemMarkersArray[key].setMap(null);
					}
				});

				// Spiel entsprechend der arhaltenen Informationen anpassen

				if (gameDidEnd) {
					$("#waitingText").html("Das Spiel ist zu Ende. Vielen Dank fürs Mitspielen.");
					stopIntervals();
					waitingForGameOverlay.show();
				} else {
					if (battery > 0) {
						 if (!gameExists && gameDidExist) {
							window.location.reload();
						} else if (!gameExists && !gameDidExist) {
							$("#waitingText").html("Warte auf Spielstart");
							stopIntervals();
							waitingForGameOverlay.show();
						} else if (gameExists && gameDidExist && !gameIsRunning) {
							$("#waitingText").html("Das Spiel wurde Pausiert");
							stopIntervals();
							waitingForGameOverlay.show();
						} else {
							startIntervals();
							waitingForGameOverlay.hide();
						}
					} else {
						$("#waitingText").html("Dein Akku ist alle :( Vielen Dank fürs Mitspielen.");
						stopIntervals();
						waitingForGameOverlay.show();
					}
				}
				
				// Ansicht aktualisieren

				updateDisplay();
			},
			error: function() {
				gameStatusRequestExecutes = false;
			}
		});
	};
}

function drawNeighbourMarkerAtLatitudeLongitude(playerId, latitude, longitude) {
	var latlng = new google.maps.LatLng(latitude, longitude);
	
	var image = new google.maps.MarkerImage('../media/images/icons/network-wireless-small.png',
	      new google.maps.Size(16, 16),
	      // The origin for this image is 0,0.
	      new google.maps.Point(0,0),
	      // The anchor for this image is the base of the flagpole at 0,32.
	      new google.maps.Point(8, 8));
	
	if (neighbourMarkersArray[playerId] == undefined) {
		var marker = new google.maps.Marker({
			position: latlng,
			map: senchaMap.map,
		    title: "("+playerId+") "+name,
			icon: image,
			zIndex: 1
		});
		
		neighbourMarkersArray[playerId] = marker;
	} else {
		neighbourMarkersArray[playerId].setPosition(latlng);
		neighbourMarkersArray[playerId].setTitle("("+playerId+") "+name);
		if (neighbourMarkersArray[playerId].map == null) { neighbourMarkersArray[playerId].setMap(senchaMap.map); };
	}
}

function drawNearbyItemMarkerAtLatitudeLongitude(itemId, type, latitude, longitude) {
	var latlng = new google.maps.LatLng(latitude, longitude);
	
	if (type == 1) {
		imagePath = "../media/images/icons/battery-charge.png";
	} else if (type == 2) {
		imagePath = "../media/images/icons/mobile-phone-cast.png";
	}
	
	
	var image = new google.maps.MarkerImage(imagePath,
	      new google.maps.Size(16, 16),
	      // The origin for this image is 0,0.
	      new google.maps.Point(0,0),
	      // The anchor for this image is the base of the flagpole at 0,32.
	      new google.maps.Point(8, 8));
	
	if (nearbyItemMarkersArray[itemId] == undefined) {
		var marker = new google.maps.Marker({
			position: latlng,
			map: senchaMap.map,
			icon: image,
			zIndex: 1
		});
		
		nearbyItemMarkersArray[itemId] = marker;
	} else {
		nearbyItemMarkersArray[itemId].setPosition(latlng);
		if (nearbyItemMarkersArray[itemId].map == null) { nearbyItemMarkersArray[itemId].setMap(senchaMap.map); };
	}
}

function updateDisplay() {
	//console.log("updateDisplay");
	
	if (!isNaN(score)) mainPanelToolbar.items["items"][0].setText(String(score));
	if (!isNaN(neighbourCount)) mainPanelToolbar.items["items"][2].setText(String(neighbourCount));
	if (!isNaN(remainingPlayingTime)) mainPanelToolbar.items["items"][4].setText(String(remainingPlayingTime));
	if (!isNaN(battery)) mainPanelToolbar.items["items"][6].setText(String(battery+"%").replace(".", ","));
	
	$("#hint").html(hint);
	
	if (serverLatitude && serverLongitude) {
		// Karte zentrieren
		senchaMap.map.setCenter(new google.maps.LatLng(serverLatitude, serverLongitude));
		// Spieler Marker zentrieren
		playerMarker.setPosition(new google.maps.LatLng(serverLatitude, serverLongitude));
		if (playerMarker.map == null) { playerMarker.setMap(senchaMap.map); };
		// Senderadius zentrieren
		playerRadius.setCenter(new google.maps.LatLng(serverLatitude, serverLongitude));
		if (playerRadius.map == null) { playerRadius.setMap(senchaMap.map); };
		playerRadius.setRadius(playerRange);
		// Sammelradius zentrieren
		collectionRadius.setCenter(new google.maps.LatLng(serverLatitude, serverLongitude));
		if (collectionRadius.map == null) { collectionRadius.setMap(senchaMap.map); };
		collectionRadius.setRadius(itemCollectionRange);
	}

	if (nextItemDistance) $("#nextItemDistance").html("Entfernung zum nächsten Gegenstand "+nextItemDistance+" Meter."); else $("#nextItemDistance").html("Keine Gegenstände in der Nähe.");
	
	if (hasRangeBooster) { boosterImageElement = "<img src='../media/images/icons/mobile-phone-cast.png' />" } else { boosterImageElement = "<img src='../media/images/icons/mobile-phone-cast-gray.png' />" };
	
	$("#activeItems").html("Aktive Gegenstände: "+boosterImageElement);
	
	if (itemInCollectionRange) {
		$("#collectItemButton").button("enable");
	} else {
		$("#collectItemButton").button("disable");
	}

	if (neighbours != undefined) {
		$.each(neighbours, function(key, value) {
			drawNeighbourMarkerAtLatitudeLongitude(key, value["latitude"], value["longitude"]);
		});
	}
	
	if (nearbyItems != undefined) {
		$.each(nearbyItems, function(key, value) {
			drawNearbyItemMarkerAtLatitudeLongitude(key, value["type"], value["latitude"], value["longitude"]);
		});
	}
}

function collectItem() {
	$("#collectItemButton").button("disable");
	$("#collectItemButton span.ui-button-text").html("Gegenstand wird eingesammelt...<img src='../media/images/ajax-loader.gif' />");
	$.ajax({
		type: "POST",
		url: "../php/ajax/mobile/collect_item.php",
		data: "playerId="+playerId,
		success: function() {
			updateDisplay();
		} 
	})
}