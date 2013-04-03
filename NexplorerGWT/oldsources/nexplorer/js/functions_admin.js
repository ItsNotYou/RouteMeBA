var map;
var items;
var players;
var itemMarkersArray = [];
var playerMarkersArray = [];
var radiusCirclesArray = [];

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

function getItems() {
	$.ajax({
		type: "POST",
		dataType: "json",
		url: "../php/json/admin/get_items.php",
		success: function(data) {
			items = data["items"];
			
			$.each(itemMarkersArray, function(key, theMarker) {
				if (theMarker != undefined && data["items"][key] == undefined) {
					itemMarkersArray[key].setMap(null);
				}
			});
			
			if (items != undefined) {
				$.each(items, function(key, value) {
					drawItemMarkerAtLatitudeLongitude(key, value["type"], value["latitude"], value["longitude"]);
				});
			}
		}
	})
}

function getPlayers() {
	$.ajax({
		type: "POST",
		dataType: "json",
		url: "../php/json/admin/get_players.php",
		success: function(data) {
			players = data["players"];
			
			$.each(playerMarkersArray, function(key, theMarker) {
				if (theMarker != undefined && data["players"][key] == undefined) {
					playerMarkersArray[key].setMap(null);
					radiusCirclesArray[key].setMap(null);
				}
			});
			
			if (players != undefined) {
				$.each(players, function(key, value) {
					drawPlayerMarkerAtLatitudeLongitude(key, parseFloat(value["latitude"]), parseFloat(value["longitude"]), value["name"], parseInt(value["range"]));
				});
			}
		}
	})
}

function drawPlayerMarkerAtLatitudeLongitude(playerId, latitude, longitude, name, signalRange) {
	var latlng = new google.maps.LatLng(latitude, longitude);
	
	var image = new google.maps.MarkerImage('../media/images/icons/network-wireless-small.png',
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

function drawItemMarkerAtLatitudeLongitude(itemId, type, latitude, longitude) {
	var latlng = new google.maps.LatLng(latitude, longitude);
	
	if (type == 1) {
		imagePath = "../media/images/icons/battery-charge.png";
		title = "Batterie";
	} else if (type == 2) {
		imagePath = "../media/images/icons/mobile-phone-cast.png";
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

function stopGame() {
	$.ajax({
		type: "POST",
		url: "../php/ajax/admin/stop_game.php",
		success: function() {
			window.location.reload();
		}
	})
}

function resumeGame() {
	$.ajax({
		type: "POST",
		url: "../php/ajax/admin/resume_game.php",
		success: function() {
			window.location.reload();
		}
	})
}

function resetGame() {
	$.ajax({
		type: "POST",
		url: "../php/ajax/admin/reset_game.php",
		success: function() {
			window.location.reload();
		}
	})
}

function getPlayerStats() {
	$.ajax({
		type: "POST",
		url: "../php/ajax/admin/get_player_stats.php",
		success: function(data) {
			$("#playerStats").html(data);
			window.setTimeout("getPlayerStats()", 1000);
		}
	})
}

function getGameStats() {
	$.ajax({
		type: "POST",
		url: "../php/ajax/admin/get_game_stats.php",
		success: function(data) {
			$("#gameStats").html(data);
			window.setTimeout("getGameStats()", 1000);
		}
	})
}

function getItemStats() {
	$.ajax({
		type: "POST",
		url: "../php/ajax/admin/get_item_stats.php",
		success: function(data) {
			$("#itemStats").html(data);
			window.setTimeout("getItemStats()", 5000);
		}
	})
}

function updateRemainingPlayingTime() {
	$.ajax({
		type: "POST",
		url: "../php/ajax/admin/update_remaining_playing_time.php",
		success: function() {
			window.setTimeout("updateRemainingPlayingTime()", 3000);
		}
	})
}

function placeNewItems() {
	$.ajax({
		type: "POST",
		url: "../php/ajax/admin/place_items.php",
		success: function() {
			window.setTimeout("placeNewItems()", 30000);
		}
	})
}

function updateBonusGoals() {
	$.ajax({
		type: "POST",
		url: "../php/ajax/admin/update_bonus_goals.php",
		success: function() {
			window.setTimeout("updateBonusGoals()", 8000);
		}
	})
}

function updateNodeBatteries() {
	$.ajax({
		type: "POST",
		url: "../php/ajax/admin/update_node_batteries.php",
		success: function() {
			window.setTimeout("updateNodeBatteries()", 5000);
		}
	})
}

// Funktionen für die Simulation des AODV Protokolls

function aodvProcessRoutingMessages() {
	$.ajax({
		type: "POST",
		url: "../php/ajax/admin/aodv_process_routing_messages.php",
		success: function(data) {
			window.setTimeout("aodvProcessRoutingMessages()", 8000);
			$("#logText").val(data+$("#logText").val());
		}
	})
}

function aodvProcessDataPackets() {
	$.ajax({
		type: "POST",
		url: "../php/ajax/admin/aodv_process_data_packets.php",
		success: function(data) {
			window.setTimeout("aodvProcessDataPackets()", 8000);
			$("#logText").val(data+$("#logText").val());
		}
	})
}