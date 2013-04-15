
/**
 * mainly legacy code from Tobias Moebert
 * has been adapted to work with a java backend
 * and gwt client wrapper
 * @author Julian Dehne 
 */
var map;
var playerMarkersArray = [];
var radiusCirclesArray = [];
var messageMarkersArray = [];
var messageUnderway = false;
var selectedSourceNode = 0;
var sourceNodeFlagMarker;
var selectedDestinationNode = 0;
var destinationNodeFlagMarker;
var bonusGoalMarker;

// Intervals

var gameStatusInterval;
var playerStatusInverval;
var updateDisplayInterval;

// Interval Ajax Request

var gameStatusRequestExecutes = false;
var playerStatusRequestExecutes = false;

// Interval Times

var updateDisplayIntervalTime = 300;

// Player data

var playerId = -1;
var score = 0;
var didLogin = false;

// Game data

var gameIsRunning;
var remainingPlayingTime;
var gameDidExist;
var gameExists;
var gameDifficulty;
var playingFieldCenterLatitude;
var playingFieldCenterLongitude;
var bonusGoal;
var gameDidEnd = 0;

var playerMarkerObservers;

// Ajax

var loginAjax;
var insertMessageAjax;

/**
 * This function deals with the login view of messager players
 */
function loginIndoor() {
    updateGameStatus(false);
    $("#loginModal").modal({
        onOpen : function(dialog) {
            dialog.overlay.fadeIn('slow', function() {
                dialog.data.show();
                dialog.container.fadeIn('slow');
            })
        },
        maxHeight : 300,
        maxWidth : 300
    });
    startGameStatusInterval();
    $("button").button();
}

/**
 * Diese Funktion dient dazu, mit gwt die ID zu syncronisieren
 * 
 * @returns {Number}
 */
function getPlayerId() {
    return playerId;
}

/**
 * Diese Funktion dient dazu, mit gwt die ID zu syncronisieren
 * 
 * @returns {Number}
 */
function setPlayerId(id) {
    playerId = id;
}

///**
// * set game Status accordingly
// */
//function setGameStatus(isRunning2, gameDidEnd2, gameExists2, gameDidExist2) {
//    gameDidEnd = gameDidEnd2;
//    isRunning = isRunning2;
//    gameDidEnd = gameDidEnd2;
//    gameExists = gameExists2;
//    gameDidExist = gameDidExist2;
//}

/**
 * login player and initialize canvas function chain a) -> loginPlayer ->
 * initialize (async = false) b) -> startGameStatusInterval (async = true) (in a
 * loop) -> updateDisplay -> updateMarkerPositions -> (resetMessageSelection,
 * (drawPlayerMarkerAtLatitudeLongitude -> drawBonusGoalMarker) ,
 * drawMessageMarkerAtLatitudeLongitude )
 * 
 * @param name
 * @param isMobile
 */
function loginPlayer(name, isMobile) {
    if (name != "") {
        $("#loginButton").button({
            label : "melde an..."
        });
        $("#loginButton").attr('disabled', 'disabled');
        if (loginAjax != undefined)
            loginAjax.abort();
        loginAjax = $.ajax({
            type : "POST",
            async : true,
            url : "../rest/loginManager/login_player_indoor",
            data : "name=" + name + "&isMobile=" + isMobile,
            success : function(data) {
                if (!isNaN(parseInt(data.id))) {
                    didLogin = true;
                    playerId = parseInt(data.id);
                    if ($.modal != undefined)
                        $.modal.close();
                    initialize(playerId, playingFieldCenterLatitude,
                        playingFieldCenterLongitude);
                }
            }
        });
    }
}

/**
 * initialize canvas
 * 
 * @param playerId
 * @param latitude
 * @param longitude
 */
function initialize(playerId, latitude, longitude) {
    // 52.39353, 13.13138
    var latlng = new google.maps.LatLng(latitude, longitude);
    var myOptions = {
        zoom : 19,
        center : latlng,
        disableDefaultUI : true,
        disableDoubleClickZoom : true,
        draggable : false,
        scrollwheel : false,
        mapTypeId : google.maps.MapTypeId.ROADMAP
    };

    map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
	
	playerMarkerObservers = new ObservableCollection();
	var neighbour = new NeighbourObserver();
	neighbour.subscribe(new NeighbourLine(map));
	playerMarkerObservers.subscribe(neighbour);
	
    updateDisplay();
}

function startGameStatusInterval() {
    if (gameStatusInterval === undefined || gameStatusInterval === null) {
        gameStatusInterval = window.setInterval("updateGameStatus(true)",
            updateDisplayIntervalTime);
    }
}

function updateGameStatus(isAsync) {
    if (gameStatusRequestExecutes === false) {
        gameStatusRequestExecutes = true;
        $.ajax({
            dataType : "json",
            async : isAsync,
            url : "../rest/gameManager/game_status",
            success : function(data) {
                gameStatusRequestExecutes = false;

                // es hat immer ein Spiel gegeben :-)
                gameDidExist = 1;
                gameIsRunning = parseInt(data["isRunning"]);
                remainingPlayingTime = parseInt(data["remainingPlayingTime"]);
                gameExists = parseInt(data["gameExists"]);
                gameDifficulty = parseInt(data["gameDifficulty"]);
                playingFieldCenterLatitude = parseFloat(data["playingFieldCenterLatitude"]);
                playingFieldCenterLongitude = parseFloat(data["playingFieldCenterLongitude"]);
                bonusGoal = parseInt(data["bonusGoal"]);
                gameDidEnd = parseInt(data["didEnd"]);
                updateDisplayIntervalTime = parseInt(data["updateDisplayIntervalTime"]);
                updateDisplay();
            }
        });
    }
}

function removeObsoleteMarkers(key) {
    playerMarkersArray[key].setMap(null);
    radiusCirclesArray[key].setMap(null);

    // Bonuszielicon entfernen
    if (bonusGoal == parseInt(key)) {
        bonusGoalMarker.setMap(null);
    }

    // wenn gerad als Ziel oder Start ausgewählt, Auswahl
    // zurücksetzen
    if (selectedSourceNode == parseInt(key)
        || selectedDestinationNode == parseInt(key)) {
        resetMessageSelection();
    }
}

function drawMarkers(theMarker) {
    drawPlayerMarkerAtLatitudeLongitude(
        parseFloat(theMarker.latitude),
        parseFloat(theMarker.longitude), theMarker.name,
        parseInt(theMarker.id), parseInt(theMarker.range),
        parseInt(theMarker.packetCount));
}

function updateMessageMarkers(theMarker) {
    if (theMarker.ownerId == playerId) {
        messageUnderway = true;
    }
    drawMessageMarkerAtLatitudeLongitude(theMarker.latitude,
        theMarker.longitude, theMarker.status, theMarker.id);
}

function removeObsoleteMessageMarkers(key) {
    messageMarkersArray[key].setMap(null);
}

/**
 * update the markers on the canvas to show the actual positions of the players
 */
function updateMarkerPositions() {
    $.ajax({
        url : "../rest/indoor/get_markers",
        dataType : "json",
        data : "playerId=" + playerId,
        success : function(data) {
            // Spieler-Marker aktualisieren
            $.each(data["playerMarkers"], function(key, theMarker) {
                drawMarkers(theMarker);
            });

            // Player Maker entfernen die nicht mehr vorhanden sind
            $.each(playerMarkersArray, function(key, theMarker) {
                if (theMarker != undefined
                    && data["playerMarkers"][key] == undefined) {
                    removeObsoleteMarkers(key);
                }
            });
			
			playerMarkerObservers.update(data["playerMarkers"]);

            messageUnderway = false;
            // Nachrichten-Marker aktualisieren
            $.each(data["messageMarkers"], function(key, theMarker) {
                updateMessageMarkers(theMarker);
            });

            $.each(messageMarkersArray, function(key, theMarker) {
                if (theMarker != undefined
                    && data["messageMarkers"][key] == undefined) {
                    removeObsoleteMessageMarkers(key);
                }
            });
        }
    });
}

/*
 * Diese Funktion zeichnet die PlayerMarkierungen auf der Karte der Indoor
 * Spieler
 */
function drawPlayerMarkerAtLatitudeLongitude(latitude, longitude, name,
    playerId, range, packetCount) {
    // Debugging
    var latlng = new google.maps.LatLng(latitude, longitude);

    if (packetCount > 3) {
        markerImagePath = 'media/images/icons/network-status-busy.png';
    } else if (packetCount > 1 && packetCount <= 3) {
        markerImagePath = 'media/images/icons/network-status-away.png';
    } else if (packetCount <= 1) {
        markerImagePath = 'media/images/icons/network-status.png';
    }

    var markerImage = new google.maps.MarkerImage(markerImagePath,
        new google.maps.Size(16, 16),
        // The origin for this image is 0,0.
        new google.maps.Point(0, 0),
        // The anchor for this image is the base of the flagpole at 0,32.
        new google.maps.Point(8, 8));

    if (playerMarkersArray[playerId] == undefined) {
        var marker = new google.maps.Marker({
            position : latlng,
            map : map,
            title : "" + playerId + "",
            icon : markerImage,
            zIndex : 2
        });

        playerMarkersArray[playerId] = marker;
    } else {
        playerMarkersArray[playerId].setPosition(latlng);
        playerMarkersArray[playerId].setTitle("" + playerId + "");
        playerMarkersArray[playerId].setIcon(markerImage);
        if (playerMarkersArray[playerId].getMap() == null) {
            playerMarkersArray[playerId].setMap(map);
        }

        google.maps.event.clearListeners(playerMarkersArray[playerId], "click");
    }

    if (playerId == selectedDestinationNode
        && destinationNodeFlagMarker != undefined) {
        destinationNodeFlagMarker.setPosition(latlng);
    }

    if (playerId == selectedSourceNode && sourceNodeFlagMarker != undefined) {
        sourceNodeFlagMarker.setPosition(latlng);
    }

    google.maps.event
    .addListener(
        playerMarkersArray[playerId],
        'click',
        createOnClick(playerId, latlng));

    // Bonusmarker zeichnen
    if (playerId == bonusGoal) {
        drawBonusGoalMarker(latlng, playerId);
    }

    drawCircleOnMapAtPositionWithRadius(latlng, range, playerId);
}

function createOnClick(playerId, latlng) {
    return function() {
        if (!messageUnderway) {
            if (!selectedSourceNode) {
                setSourceFlag(playerId,latlng);
            } else if (!selectedDestinationNode
                && playerMarkersArray
                .indexOf(playerMarkersArray[playerId]) != selectedSourceNode) {
                setDestinationFlag(playerId,latlng);
            } else {
                resetMessageSelection();
                setSourceFlag(playerId,latlng);
            }
            // updatePlayerStatus();
        }
    };
}

function setSourceFlag(playerId,latlng) {
    selectedSourceNode = playerMarkersArray
    .indexOf(playerMarkersArray[playerId]);

    if (sourceNodeFlagMarker == undefined) {
        var startNodeImage = new google.maps.MarkerImage(
            'media/images/icons/flag-white.png',
            new google.maps.Size(16, 16),
            new google.maps.Point(0, 0),
            new google.maps.Point(14, 12));

        sourceNodeFlagMarker = new google.maps.Marker(
        {
            position : latlng,
            map : map,
            icon : startNodeImage,
            zIndex : 2
        });
    } else {
        sourceNodeFlagMarker.setPosition(latlng);
        if (sourceNodeFlagMarker.getMap() == null) {
            sourceNodeFlagMarker.setMap(map);
        }
    }
}


function setDestinationFlag(playerId, latlng) {
    selectedDestinationNode = playerMarkersArray
    .indexOf(playerMarkersArray[playerId]);

    if (destinationNodeFlagMarker == undefined) {
        var destinationNodeImage = new google.maps.MarkerImage(
            'media/images/icons/flag-black.png',
            new google.maps.Size(16, 16),
            new google.maps.Point(0, 0),
            new google.maps.Point(14, 12));

        destinationNodeFlagMarker = new google.maps.Marker(
        {
            position : latlng,
            map : map,
            icon : destinationNodeImage,
            zIndex : 3
        });
    } else {
        destinationNodeFlagMarker
        .setPosition(latlng);
        if (destinationNodeFlagMarker.getMap() == null) {
            destinationNodeFlagMarker.setMap(map);
        }
    }
}


function drawBonusGoalMarker(latlng, playerId) {
    var bonusGoalImage = new google.maps.MarkerImage(
        'media/images/icons/star-small.png', new google.maps.Size(16, 16),
        new google.maps.Point(0, 0), new google.maps.Point(-4, 12));

    if (bonusGoalMarker == undefined) {
        bonusGoalMarker = new google.maps.Marker({
            position : latlng,
            map : map,
            icon : bonusGoalImage,
            zIndex : 3
        });
    } else {
        bonusGoalMarker.setPosition(latlng);
        if (bonusGoalMarker.getMap() == null) {
            bonusGoalMarker.setMap(map);
        }
    ;
    }
}

function drawCircleOnMapAtPositionWithRadius(position, radius, markerId) {
    // Debugging
    // console.log("drawCircleOnMapAtPositionWithRadius at "+position+" radius
    // "+radius+" for "+markerId);
    if (radius > 0) {
        if (radiusCirclesArray[markerId] == undefined) {
            circle = new google.maps.Circle({
                center : position,
                radius : radius,
                strokeColor : "#0000FF",
                strokeOpacity : 0.35,
                strokeWeight : 2,
                fillColor : "#0000FF",
                fillOpacity : 0.20,
                map : map,
                zIndex : 1,
                clickable : false
            });

            radiusCirclesArray[markerId] = circle;
        } else {
            radiusCirclesArray[markerId].setCenter(position);
            radiusCirclesArray[markerId].setRadius(parseInt(radius));
            if (radiusCirclesArray[markerId].getMap() == null) {
                radiusCirclesArray[markerId].setMap(map);
            }
        }
    }
}

function drawMessageMarkerAtLatitudeLongitude(latitude, longitude, status,
    messageId) {
    var latlng = new google.maps.LatLng(latitude, longitude);
    var imagePath;
    var title;

    switch (status) {
        case 1:
            imagePath = "media/images/icons/mail.png";
            title = "Unterwegs";
            break;
        case 2:
            imagePath = "media/images/icons/mail--tick.png";
            title = "Am Ziel";
            break;
        case 3:
            imagePath = "media/images/icons/mail--slash.png";
            title = "Übertragungsfehler";
            break;
        case 4:
            imagePath = "media/images/icons/mail--exclamation.png";
            title = "Wartet auf Wegfindung";
            break;
        case 5:
            imagePath = "media/images/icons/mail--clock.png";
            title = "Wartet weil Knoten beschäftigt";
            break;
    }

    var image = new google.maps.MarkerImage(imagePath, new google.maps.Size(16,
        16), new google.maps.Point(0, 0), new google.maps.Point(-4, -4));

    if (messageMarkersArray[messageId] == undefined) {
        var marker = new google.maps.Marker({
            position : latlng,
            map : map,
            icon : image,
            title : title,
            zIndex : 3
        });

        messageMarkersArray[messageId] = marker;
    } else {
        // maybe the positions are out of bounds
        messageMarkersArray[messageId].setPosition(latlng);
        messageMarkersArray[messageId].setIcon(image);
        messageMarkersArray[messageId].setTitle(title);
        if (messageMarkersArray[messageId].getMap() == null) {
            messageMarkersArray[messageId].setMap(map);
        }
    }
}

/**
 * Diese Funktion wird aufgerufen, wenn auf neue Nachricht geclickt wurde
 */
function resetPlayerMessage() {
    $.ajax({
        url : "../rest/indoor/reset_player_message",
        type : "POST",
        data : "playerId=" + playerId,
        success : function(data) {
            resetMessageSelection();
        }
    })
}

/**
 * this function used to be called in order to resend Routing Messages however
 */
function resendRoutingMessages() {
    $("#resendButton").attr('disabled', 'disabled');
    $("#resendButton").unbind("click");
    $.ajax({
        url: "../rest/indoor/resend_route_request",
        type: "POST",
        data:
        "playerId="+playerId,
        success: function(data) {
            // updatePlayerStatus();
        }
    })
}

function resetMessageSelection() {
    messageUnderway = false;
    selectedSourceNode = 0;
    selectedDestinationNode = 0;    
    // updatePlayerStatus();
    sourceNodeFlagMarker.setMap(null);
    destinationNodeFlagMarker.setMap(null);
    updateDisplay();
}

/**
 * 
 * @param sourceNodeId
 * @param destinationNodeId
 */
function insertNewMessage() {
    $("#sendMessageButton").attr('disabled', 'disabled');
    $("#sendMessageButton").unbind("click");
    if (insertMessageAjax != undefined)
        insertMessageAjax.abort();
    insertMessageAjax = $.ajax({
        url : "../rest/indoor/insert_new_message",
        type : "POST",
        data : "ownerId=" + playerId + "&sourceNodeId=" + selectedSourceNode
        + "&destinationNodeId=" + selectedDestinationNode,
        success : function(data) {
            updateDisplay();
        }
    })
}

/**
 * update marker positions if game is running put modal if game is paused say
 * thank you if game is over
 */
function updateDisplay() {
    // console.log("didLogin "+didLogin+" gameExists "+gameExists+" gameDidExist
    // "+gameDidExist+" gameIsRunning "+gameIsRunning);
    if (didLogin) {
        if (gameDidEnd) {
            $("#waitingText").html(
                "Das Spiel ist zu Ende. Danke fürs Mitspielen!");
            $("#waitingOverlay").modal();
        } else {
            if (!gameExists && gameDidExist) {
                window.location.reload();
            } else if (gameExists && gameDidExist && !gameIsRunning) {
                $("#waitingText").html("Bitte warten. Spiel wurde pausiert.")
                $("#waitingOverlay").modal();
            } else if (gameExists && gameDidExist && gameIsRunning) {
                if ($.modal != undefined)
                    $.modal.close();

                // updatePlayerStatus();
                updateMarkerPositions();
            }
        }
    } else {
        // original wurde hier auch auf && !gameDidExist geprüft
        if (gameExists) {
            $("#loginNotReady").hide();
            $("#loginReady").show();
        } else if (!gameExists && gameDidExist) {
            $("#loginNotReady").show();
            $("#loginReady").hide();
        }
    }
}