/**
 * This script contains the legacy code for the admin
 * interface.
 * It mainly serves the purpose of drawing the markers at
 * the map.
 */

var map;
var items;
var players;
var itemMarkersArray = [];
var playerMarkersArray = [];
var radiusCirclesArray = [];
var gamePaused = true;
var gameEnded = false;

function setEnded() {
    gameEnded = true;
    showGameEnded();
}

function setNotEnded() {
    gameEnded = false;
}

function setPaused() {
	gamePaused = true;    
}

function setNotPaused() {
	gamePaused = false;
}


/**
 * initialize the map of the playing field
 * @param latitude
 * @param longitude
 * @param playingFieldUpperLeftLatitude
 * @param playingFieldUpperLeftLongitude
 * @param playingFieldLowerRightLatitude
 * @param playingFieldLowerRightLongitude
 */
function initializeAdminMap(latitude, longitude, playingFieldUpperLeftLatitude, playingFieldUpperLeftLongitude, playingFieldLowerRightLatitude, playingFieldLowerRightLongitude) {
	// 52.39353, 13.13138
    var latlng = new google.maps.LatLng(latitude, longitude);
    var myOptions = {
		zoom: 19,
      	center: latlng,
	  	disableDefaultUI: true,
	  	disableDoubleClickZoom: true,
		draggable: false,
		scrollwheel: false,
      	mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    
	map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
	
	southWestLatLng = new google.maps.LatLng(playingFieldLowerRightLatitude, playingFieldUpperLeftLongitude);
	northEastLatLng = new google.maps.LatLng(playingFieldUpperLeftLatitude, playingFieldLowerRightLongitude);
	playingFieldBounds = new google.maps.LatLngBounds(southWestLatLng, northEastLatLng);
	
	var rectOptions = {
		strokeColor: "#FF0000",
		strokeOpacity: 0.8,
		strokeWeight: 2,
		fillOpacity: 0,
		map: map,
		bounds: playingFieldBounds,
		clickable: false,
		zIndex: 1
	};

	playingField = new google.maps.Rectangle(rectOptions);
	
	getItems();
	getPlayers();
	window.setInterval("getItems()", 3000);
	window.setInterval("getPlayers()", 3000);
}

/**
 * get the items and draw them
 */
function getItems() {
	$.ajax({
		type: "POST",
		dataType: "json",
		url: "../rest/game_events/get_items",
		success: function(data) {
			items = data["items"];
			
			$.each(itemMarkersArray, function(key, theMarker) {
				if (theMarker != undefined && data["items"][key] == undefined) {
					itemMarkersArray[key].setMap(null);
				}
			});
			
			if (items != undefined) {
				$.each(items, function(key, value) {
					drawItemMarkerAtLatitudeLongitude(key, value["itemType"], value["latitude"], value["longitude"]);
				});
			}
		}
	})
}

/*  
 *  fetches the players status from the server and / draws markers for all the players
 */
function getPlayers() {
	$.ajax({
		type: "POST",
		dataType: "json",
		url: "../rest/game_events/get_players",
		success: function(data) {
			players = data["players"];
			// remove all markers on the map
			$.each(playerMarkersArray, function(key, theMarker) {
				if (theMarker != undefined && data["players"][key] == undefined) {
					playerMarkersArray[key].setMap(null);
					radiusCirclesArray[key].setMap(null);
				}
			});
			// draw new markers
			if (players != undefined) {
				$.each(players, function(key, value) {
					drawPlayerMarkerAtLatitudeLongitude(key, parseFloat(value["latitude"]), parseFloat(value["longitude"]), value["name"], parseInt(value["range"]));
				});
			}
		} 
	})
}

/**
 * draw the player at their respective position
 * @param playerId
 * @param latitude
 * @param longitude
 * @param name
 * @param signalRange
 */
function drawPlayerMarkerAtLatitudeLongitude(playerId, latitude, longitude, name, signalRange) {
	var latlng = new google.maps.LatLng(latitude, longitude);
	
	var image = new google.maps.MarkerImage('media/images/icons/network-wireless-small.png',
	      new google.maps.Size(16, 16),
	      // The origin for this image is 0,0.
	      new google.maps.Point(0,0),
	      // The anchor for this image is the base of the flagpole at 0,32.
	      new google.maps.Point(8, 8));
	
	if (playerMarkersArray[playerId] == undefined) {
		var marker = new google.maps.Marker({
			position: latlng,
			map: map,
		    title: "("+playerId+") "+name,
			icon: image,
			zIndex: 2
		});
		
		playerMarkersArray[playerId] = marker;
	} else {
		playerMarkersArray[playerId].setPosition(latlng);
		playerMarkersArray[playerId].setTitle("("+playerId+") "+name);
		if (playerMarkersArray[playerId].map == null) { playerMarkersArray[playerId].setMap(map); };
	}
	
	drawCircleOnMapAtPositionWithRadius(latlng, signalRange, playerId);
}

/**
 * draw circles around their position
 * @param position
 * @param radius
 * @param markerId
 */
function drawCircleOnMapAtPositionWithRadius(position, radius, markerId) {
	// Debugging
	//console.log("drawCircleOnMapAtPositionWithRadius at "+position+" radius "+radius+" for "+markerId);
	
	if(radius > 0) {
		if (radiusCirclesArray[markerId] == undefined) {
			circle = new google.maps.Circle({
		        center: position,
		        radius: radius,
		        strokeColor: "#0000FF",
		        strokeOpacity: 0.35,
		        strokeWeight: 2,
		        fillColor: "#0000FF",
		        fillOpacity: 0.20,
		        map: map,
				zIndex: 1,
				clickable: false
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

/**
 * draw the items
 * @param itemId
 * @param type
 * @param latitude
 * @param longitude
 */
function drawItemMarkerAtLatitudeLongitude(itemId, type, latitude, longitude) {
	var latlng = new google.maps.LatLng(latitude, longitude);
	
	if (type == "BATTERY") {
		imagePath = "media/images/icons/battery-charge.png";
		title = "Batterie";
	} else if (type == "BOOSTER") {
		imagePath = "media/images/icons/mobile-phone-cast.png";
		title = "Booster";
	}
	
	
	var image = new google.maps.MarkerImage(imagePath,
	      new google.maps.Size(16, 16),
	      // The origin for this image is 0,0.
	      new google.maps.Point(0,0),
	      // The anchor for this image is the base of the flagpole at 0,32.
	      new google.maps.Point(8, 8));
	
	if (itemMarkersArray[itemId] == undefined) {
		var marker = new google.maps.Marker({
			position: latlng,
			map: map,
			icon: image,
			title: title+" ("+itemId+")",
			zIndex: 2
		});
		
		itemMarkersArray[itemId] = marker;
	} else {
		itemMarkersArray[itemId].setPosition(latlng);
		if (itemMarkersArray[itemId].map == null) { itemMarkersArray[itemId].setMap(map); };
	}
}

/*
Diese Funktion wurde umdefiniert. Sie fungiert nun
als toggle: Entweder Spiel pausieren oder eben fortsetzen
Wenn ein Spiel pausiert ist, werden keine neue Routingrunden
gestartet
 */
function resumeGame() {
    if (gamePaused) {
    $.ajax({
		type: "POST",
		url: "../rest/gameManager/resume",
		success: function() {
			//window.location.reload();
                $('#resumeButton').text("Spiel pausieren");
		}
	})
    } else {
        $.ajax({
            type: "POST",
            url: "../rest/gameManager/pause",
            success: function() {
                //window.location.reload();
                $('#resumeButton').text("Spiel fortsetzen");
            }
        })
    }    
}

/**
 * beim ersten aufruf hält diese Methode das Spiel an
 * beim zweiten Aufruf wird das Spiel gelöscht
 */
function resetGame() {
   if (gameEnded) {
	$.ajax({
		type: "POST",
		url: "../rest/gameManager/reset",
		success: function() {
			window.location.reload();
		}
	})
   } else {
       $.ajax({
           type: "POST",
           url: "../rest/gameManager/stop",
           success: function() {
        	   setEnded();       
           }
       })
   }   
}

function showGameEnded() {
    $("#resetButton").text("Spiel löschen");
    $("#resumeButton").hide();
}

function startGame() {
    window.location.reload();
}
