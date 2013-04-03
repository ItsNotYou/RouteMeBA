/**
 * mainly legacy code from Tobias Moebert has been adapted to work with a java
 * backend and gwt client wrapper
 * 
 * @author Julian Dehne
 */

var senchaMap;
var playerMaker;
var playerRadius;
var collectionRadius;
// TODO: Parameter flexibilisieren
var minAccuracy = 11;

var neighbourMarkersArray = [];
var nearbyItemMarkersArray = [];

// Intervals

var localisationInterval;
var gameStatusInterval;
var displayMarkerInterval;

// Interval Ajax Request

var positionRequestExecutes = false;
var gameStatusRequestExecutes = false;
var playerStatusRequestExecutes = false;
var neighboursRequestExecutes = false;

// Interval Times

var updatePositionIntervalTime = 300;
var updateDisplayIntervalTime = 300;

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
var serverLongitude;
var gpsLatitude; // fixed error with gps latitude
var gpsLongitude;
var gpsAccuracy;
var gpsSpeed;
var gpsHeading;
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
var gameDidExist = 1; // die semantik davon, dass es mal ein Spiel gegeben
// hat, ist mir unklar ... es hat hat schon immer ein
// Spiel gegeben!
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

Ext
    .setup({
        icon : 'icon.png',
        glossOnIcon : false,
        tabletStartupScreen : 'tablet_startup.png',
        phoneStartupScreen : 'phone_startup.png',
        onReady : function() {
            var loginOverlayToolbar = new Ext.Toolbar({
                dock : 'top',
                title : 'Anmeldung'
            });

            loginOverlay = new Ext.Panel({
                floating : true,
                modal : true,
                centered : true,
                width : 300,
                height : 300,
                styleHtmlContent : true,
                dockedItems : loginOverlayToolbar,
                hideOnMaskTap : false,
                scroll : 'vertical',
                contentEl : 'login',
                cls : 'htmlcontent'
            });

            waitingForGameOverlay = new Ext.Panel({
                floating : true,
                modal : true,
                centered : true,
                width : 150,
                height : 150,
                styleHtmlContent : true,
                hideOnMaskTap : false,
                scroll : 'vertical',
                contentEl : 'waiting',
                cls : 'htmlcontent'
            });

            noPositionOverlay = new Ext.Panel({
                floating : true,
                modal : true,
                centered : true,
                width : 150,
                height : 150,
                styleHtmlContent : true,
                hideOnMaskTap : false,
                scroll : 'vertical',
                contentEl : 'noPosition',
                cls : 'htmlcontent'
            });

            var showLoginOverlay = function(btn, event) {
                loginOverlay.show();
            };

            var hideLoginOverlay = function(btn, event) {
                loginOverlay.hide();
            }

            mainPanelToolbar = new Ext.Toolbar({
                dock : 'top',
                items : [ {
                    iconMask : false,
                    ui : 'plain',
                    text : "0",
                    icon : "media/images/icons/point.png",
                    disabled : true,
                    disabledCls : ''
                }, {
                    xtype : 'spacer'
                }, {
                    iconMask : false,
                    ui : 'plain',
                    text : "0",
                    icon : "media/images/icons/neighborhood.png",
                    disabled : true,
                    disabledCls : ''
                }, {
                    xtype : 'spacer'
                }, {
                    iconMask : false,
                    ui : 'plain',
                    text : "0",
                    icon : "media/images/icons/clock.png",
                    disabled : true,
                    disabledCls : ''
                }, {
                    xtype : 'spacer'
                }, {
                    iconMask : false,
                    ui : 'plain',
                    text : "0%",
                    icon : "media/images/icons/battery-full.png",
                    disabled : true,
                    disabledCls : ''
                } ]
            });

            var image = new google.maps.MarkerImage(
                'media/images/icons/home-network.png',
                new google.maps.Size(16, 16), new google.maps.Point(0,
                    0), new google.maps.Point(8, 8));

            var position = new google.maps.LatLng(37.44885, -122.158592)

            senchaMap = new Ext.Map({
                useCurrentLocation : false,
                mapOptions : {
                    zoom : 19,
                    mapTypeId : google.maps.MapTypeId.ROADMAP,
                    navigationControl : false,
                    disableDefaultUI : true,
                    disableDoubleClickZoom : true,
                    draggable : false,
                    scrollwheel : false
                }
            });

            playerMarker = new google.maps.Marker({
                title : 'My Current Location',
                icon : image,
                zIndex : 1
            })

            playerRadius = new google.maps.Circle({
                strokeColor : "#0000FF",
                strokeOpacity : 0.35,
                strokeWeight : 2,
                fillColor : "#0000FF",
                fillOpacity : 0.20
            });

            collectionRadius = new google.maps.Circle({
                strokeColor : "#FF0000",
                strokeOpacity : 0.35,
                strokeWeight : 1,
                fillColor : "#FF0000",
                fillOpacity : 0.25
            });

            var mainContent = {
                style : "width:100%; background-color:#C9DAEF; font-size:12px; text-align: center; padding: 3px",
                title : "mainContent",
                id : "mainContent",
                html : "<div id='nextItemDistance' style='width:100%'></div><div id='activeItems' style='width:100%'></div><div style='width:100%'><button id='collectItemButton' type='button' disabled='disabled' onclick='collectItem()' />Gegenstand einsammeln</button></div><div class='ui-state-highlight ui-corner-all' style='width:100%'><span class='ui-icon ui-icon-info' style='float: left; margin-right: .3em;'></span><span id='hint'></span></div>",
                flex : 0.5
            };

            mainPanel = new Ext.Panel({
                fullscreen : true,
                dockedItems : [ mainPanelToolbar ],
                items : [ senchaMap, mainContent ],
                layoutOnOrientationChange : false,
                layout : {
                    type : "vbox",
                    align : "stretch",
                    pack : "center"
                },
                defaults : {
                    flex : 1
                }
            })

            showLoginOverlay();
            $("button").button();
        }
    });

/**
 * Dise Funktion wird zunächst aufgerufen sie loggt den spier ein und zeigt bei
 * existierenden Spiel eine Karte
 * 
 * @param name
 * @param isMobile
 */
function loginPlayer(name, isMobile) {
    if (name != "") {
        $("#loginButton").button({
            label : "melde an..."
        });
        //		$("#loginButton").attr('disabled', 'disabled');
        $.ajax({
            type : "POST",
            url : "../rest/loginManager/login_player_mobile",
            data : "name=" + name + "&isMobile=" + isMobile,
            success : function(data) {
                if (!isNaN(parseInt(data.id))) {
                    playerId = parseInt(data.id);
                    loginOverlay.hide();
                    updateGameStatus(false);
                    startGameStatusInterval();
                    // $("#mainContent").html("");					
                } else {
                    showLoginError("Keine id bekommen");
                }
            },
            error: function() {
                showLoginError("Exception wurde ausgelößt - Kein Spiel gestartet?");
            }			
        });
    }
}

function showLoginError(data) {
    // $("#mainContent").html(
	//     "Anmeldung fehlgeschlagen! <br/><br/>"
	//     + data);
    $("#beginDialog").html("Kein Spiel da. Versuchen Sie es später noch einmal!");
    $("#loginButton").button({
        label : "anmelden "
    });
}


/**
 * bewirkt, dass das Display regelmäßig aktualisiert wird und die aktuelle
 * Position an den Server gesendet wird
 */
function startIntervals() {
    startGameStatusInterval();
    startLocalisationInterval();
    startDisplayInterval();
}

function stopIntervals() {
    clearInterval(localisationInterval);
    localisationInterval = null;

    navigator.geolocation.clearWatch(positionWatch);
    positionWatch = null;
}

function startGameStatusInterval() {
    if (gameStatusInterval === undefined || gameStatusInterval === null) {
        gameStatusInterval = window.setInterval("updateGameStatus(true)",
            updateDisplayIntervalTime);
    }
}

function startLocalisationInterval() {
    if (localisationInterval === undefined || localisationInterval === null) {
        localisationInterval = window.setInterval("updatePosition()",
            updatePositionIntervalTime);
    }
}

function startDisplayInterval() {
    if (displayMarkerInterval === undefined || displayMarkerInterval === null) {
        displayMarkerInterval = window.setInterval("updateDisplay()", 1);
    }
}

/**
 * sendet die aktuelle Positionsdaten an den Server
 */
function updatePosition() {
    if (positionRequestExecutes === false && gpsLatitude != undefined
        && gpsLongitude != undefined) {
        positionRequestExecutes = true;
        updatePositionStartTime = new Date().getTime();
        $.ajax({
            type : "POST",
            url : "../rest/mobile/update_player_position",
            data : "latitude=" + gpsLatitude + "&longitude=" + gpsLongitude + "&accuracy=" + gpsAccuracy
            + "&playerId=" + playerId + "&speed=" + gpsSpeed + "&heading=" + gpsHeading,
            timeout : 5000,
            success : function() {
                latencyCount++;
                latencyTotal += new Date().getTime() - updatePositionStartTime;
                // console.log("Count: " + latencyCount + " Latenz: " +
                // (latencyTotal / latencyCount));
                positionRequestExecutes = false;
            }
        });
    };
}

/*
 * // findet in höheren Schwierigkeitsgraden Verwendung function
 * updateNeighbours() { if (neighboursRequestExecutes === false) {
 * neighboursRequestExecutes = true; $.ajax({ type:"POST", data:"playerId=" +
 * playerId, url:"../php/ajax/mobile/update_neighbours.php", timeout:5000,
 * success:function () { neighboursRequestExecutes = false; } }) } ; }
 */

/**
 * callback for the geolocation
 */
function positionReceived(location) {
	// TODO: Failswitch einbauen, um Warnung bei zu lange ausbleibenden Positionen anzuzeigen
	if (location.coords.accuracy > minAccuracy) {
		return;
	}
	
    noPositionOverlay.hide();
    gpsLatitude = location.coords.latitude;
    gpsLongitude = location.coords.longitude;
	gpsAccuracy = location.coords.accuracy;
	gpsSpeed = location.coords.speed; 
	gpsHeading = location.coords.heading;
}

/**
 * callback for the geolocation
 */
function positionError(error) {
    noPositionOverlay.show();
}

/**
 * diese methode holt sich regelmäßig (alle 5000ms) ein update from server ob
 * des aktuellen Spielstandes
 * 
 * @param isAsync
 */
function updateGameStatus(isAsync) {
    // console.log("updateGameStatus async: "+isAsync);
    if (gameStatusRequestExecutes === false) {
        // console.log("gameStatusRequestExecutes === false");
        gameStatusRequestExecutes = true;
        updateGameStatusStartTime = new Date().getTime();
        $
        .ajax({
            dataType : "json",
            url : "../rest/mobile/get_game_status",
            async : isAsync,
            data : "playerId=" + playerId,
            timeout : 5000,
            success : function(data) {
                latencyCount++;
                latencyTotal += new Date().getTime()
                - updateGameStatusStartTime;
                gameStatusRequestExecutes = false;

                // Spielstatus und Spielinformationen

                gameIsRunning = parseInt(data.stats.settings["isRunning"]);
                remainingPlayingTime = parseInt(data.stats["remainingPlayingTime"]);
                gameExists = parseInt(data.stats["gameExists"]);
                gameDidExist = gameExists;
                baseNodeRange = parseInt(data.stats["baseNodeRange"]);
                gameDifficulty = parseInt(data.stats["gameDifficulty"]);
                itemCollectionRange = parseInt(data.stats.settings["itemCollectionRange"]);
                gameDidEnd = parseInt(data.stats["didEnd"]);
//                updatePositionIntervalTime = parseInt(data.stats.settings["updatePositionIntervalTime"]);
                updateDisplayIntervalTime = parseInt(data.stats.settings["updateDisplayIntervalTime"]);

                // Spielerinformationen
                battery = parseFloat(data.node["batterieLevel"]);
                neighbourCount = parseInt(data.node["neighbourCount"]);
                serverLatitude = parseFloat(data.stats["playingFieldCenterLatitude"]);
                serverLongitude = parseFloat(data.stats["playingFieldCenterLongitude"]);
                score = parseInt(data.node["score"]);
                playerRange = parseInt(data.node["range"]);
                neighbours = data.node["neighbours"];
                nearbyItemsCount = parseInt(data.node["nearbyItemsCount"]);
                nearbyItems = data.node["nearbyItems"];
                nearbyItems = nearbyItems.items;
                nextItemDistance = parseInt(data.node["nextItemDistance"]);
                itemInCollectionRange = data.node["itemInCollectionRange"];						
                hasRangeBooster = parseInt(data.node["hasRangeBooster"]);
                hint = data["hint"];

                $
                .each(
                    neighbourMarkersArray,
                    function(key, theMarker) {
                        if (theMarker != undefined
                            && neighbours[key] == undefined) {
                            neighbourMarkersArray[key]
                            .setMap(null);
                        }
                    });

                $
                .each(
                    nearbyItemMarkersArray,
                    function(key, theMarker) {
                        if (theMarker != undefined
                            && nearbyItems[key] == undefined) {
                            nearbyItemMarkersArray[key]
                            .setMap(null);
                        }
                    });

                // Spiel entsprechend der erhaltenen Informationen
                // anpassen
                if (gameDidEnd) {
                    $("#waitingText")
                    .html(
                        "Das Spiel ist zu Ende. Vielen Dank fürs Mitspielen.");
                    stopIntervals();
                    waitingForGameOverlay.show();
                } else {
                    if (battery > 0) {
                        if (!gameExists && gameDidExist) {
                            window.location.reload();
                        } else if (!gameExists && !gameDidExist) {
                            $("#waitingText").html(
                                "Warte auf Spielstart");
                            stopIntervals();
                            startGameStatusInterval();
                            waitingForGameOverlay.show();
                        } else if (gameExists && gameDidExist
                            && !gameIsRunning) {
                            $("#waitingText").html(
                                "Das Spiel wurde Pausiert");                                                        
                            stopIntervals();
                            startGameStatusInterval();
                            waitingForGameOverlay.show();
                        } else {
                            stopIntervals();
                            startIntervals();
                            waitingForGameOverlay.hide();
                        }
                    } else {
                        $("#waitingText")
                        .html(
                            "Dein Akku ist alle :( Vielen Dank fürs Mitspielen.");
                        stopIntervals();
                        waitingForGameOverlay.show();
                    }
                }

            // Ansicht aktualisieren

            // updateDisplay(); refaktorisiert.... display soll
            // nicht immer nur nach den server calls refreshed
            // werden
            },
            error: function(data) {
                gameStatusRequestExecutes = false;
                showLoginError("Exception wurde ausgelößt - Kein Spiel gestartet?" + data);						
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
function drawNeighbourMarkerAtLatitudeLongitude(playerId, latitude, longitude) {
    var latlng = new google.maps.LatLng(latitude, longitude);

    var image = new google.maps.MarkerImage(
        'media/images/icons/network-wireless-small.png',
        new google.maps.Size(16, 16),
        // The origin for this image is 0,0.
        new google.maps.Point(0, 0),
        // The anchor for this image is the base of the flagpole at 0,32.
        new google.maps.Point(8, 8));

    if (neighbourMarkersArray[playerId] == undefined) {
        var marker = new google.maps.Marker({
            position : latlng,
            map : senchaMap.map,
            title : "(" + playerId + ") " + name,
            icon : image,
            zIndex : 1
        });

        neighbourMarkersArray[playerId] = marker;
    } else {
        neighbourMarkersArray[playerId].setPosition(latlng);
        neighbourMarkersArray[playerId].setTitle("(" + playerId + ") " + name);
        if (neighbourMarkersArray[playerId].map == null) {
            neighbourMarkersArray[playerId].setMap(senchaMap.map);
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
function drawNearbyItemMarkerAtLatitudeLongitude(itemId, type, latitude,
    longitude) {
    var latlng = new google.maps.LatLng(latitude, longitude);

    if (type == "BATTERY") {
        imagePath = "media/images/icons/battery-charge.png";
    } else {
        imagePath = "media/images/icons/mobile-phone-cast.png";
    }

    var image = new google.maps.MarkerImage(imagePath, new google.maps.Size(16,
        16),
    // The origin for this image is 0,0.
    new google.maps.Point(0, 0),
        // The anchor for this image is the base of the flagpole at 0,32.
        new google.maps.Point(8, 8));

    if (nearbyItemMarkersArray[itemId] == undefined) {
        var marker = new google.maps.Marker({
            position : latlng,
            map : senchaMap.map,
            icon : image,
            zIndex : 1
        });

        nearbyItemMarkersArray[itemId] = marker;
    } else {
        nearbyItemMarkersArray[itemId].setPosition(latlng);
        if (nearbyItemMarkersArray[itemId].map == null) {
            nearbyItemMarkersArray[itemId].setMap(senchaMap.map);
        }
    ;
    }
}

/**
 * 
 * @param ms
 * @returns {String}
 */
function convertMS(s) {

    function addZ(n) {
        return (n<10? '0':'') + n;
    }

    var ms = s % 1000;
    s = (s - ms) / 1000;
    var secs = s % 60;
    s = (s - secs) / 60;
    var mins = s % 60;
    var hrs = (s - mins) / 60;

    return  addZ(mins);
}

/**
 * updates the display with the new position and the positions of the neighbours
 */
function updateDisplay() {
    // console.log("updateDisplay");
    if (positionWatch == null) {
        positionWatch = navigator.geolocation.watchPosition(positionReceived,
            positionError, {
                enableHighAccuracy : true,
                maximumAge : 0,
                timeout : 9000
            });
    }
    if (!isNaN(score))
        mainPanelToolbar.items["items"][0].setText(String(score));
    if (!isNaN(neighbourCount))
        mainPanelToolbar.items["items"][2].setText(String(neighbourCount));
    if (!isNaN(remainingPlayingTime))
        mainPanelToolbar.items["items"][4]
        .setText(convertMS(remainingPlayingTime));
    if (!isNaN(battery))
        mainPanelToolbar.items["items"][6].setText(String(battery + "%")
            .replace(".", ","));

    $("#hint").html(hint);

    if (gpsLatitude && gpsLongitude) {
        // Karte zentrieren
        senchaMap.map.setCenter(new google.maps.LatLng(gpsLatitude,
            gpsLongitude));
        // Spieler Marker zentrieren
        playerMarker.setPosition(new google.maps.LatLng(gpsLatitude,
            gpsLongitude));
        if (playerMarker.map == null) {
            playerMarker.setMap(senchaMap.map);
        }
        // Senderadius zentrieren
        playerRadius
        .setCenter(new google.maps.LatLng(gpsLatitude, gpsLongitude));
        if (playerRadius.map == null) {
            playerRadius.setMap(senchaMap.map);
        }
        playerRadius.setRadius(playerRange);
        // Sammelradius zentrieren
        collectionRadius.setCenter(new google.maps.LatLng(gpsLatitude,
            gpsLongitude));
        if (collectionRadius.map == null) {
            collectionRadius.setMap(senchaMap.map);
        }
        collectionRadius.setRadius(itemCollectionRange)
    }

    if (nextItemDistance)
        $("#nextItemDistance").html(
            "Entfernung zum nächsten Gegenstand " + nextItemDistance
            + " Meter.");
    else
        $("#nextItemDistance").html("Keine Gegenstände in der Nähe.");

    if (hasRangeBooster) {
        boosterImageElement = "<img src='media/images/icons/mobile-phone-cast.png' />"
    } else {
        boosterImageElement = "<img src='media/images/icons/mobile-phone-cast-gray.png' />"
    }

    $("#activeItems").html("Aktive Gegenstände: " + boosterImageElement);

    var isDisabled = $("#collectItemButton").is(":disabled");
    if (itemInCollectionRange && isDisabled) {
        $("#collectItemButton").button("enable");
    } else if (!itemInCollectionRange && !isDisabled) {
        $("#collectItemButton").button("disable");
    }

    if (neighbours != undefined) {
        $.each(neighbours, function(key, value) {
            drawNeighbourMarkerAtLatitudeLongitude(key, value["latitude"],
                value["longitude"]);
        });
    }

    if (nearbyItems != undefined) {
        $.each(nearbyItems, function(key, value) {
            drawNearbyItemMarkerAtLatitudeLongitude(key, value["itemType"],
                value["latitude"], value["longitude"]);
        });
    }
}

/**
 * collect items
 */
function collectItem() {
    $("#collectItemButton").button("disable");
    $("#collectItemButton span.ui-button-text")
    .html(
        "Gegenstand wird eingesammelt...<img src='media/images/ajax-loader.gif' />");
    $.ajax({
        type : "POST",
        url : "../rest/mobile/collect_item",
        data : "playerId=" + playerId,
        success : function() {
            updateDisplay();
        }
    })
}