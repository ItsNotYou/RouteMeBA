function NeighbourLine(map) {
	
	var lines = {};
	var map = map;
	
	this.added = function(key, nodes) {
		var from = new google.maps.LatLng(nodes.from.latitude, nodes.from.longitude);
		var to = new google.maps.LatLng(nodes.to.latitude, nodes.to.longitude);
		
		var line = new google.maps.Polyline(
		{
			path: [from, to],
			map: map,
			clickable: false
		});
		
		lines[key] = line;
	};
	
	this.remained = function(key, nodes) {
		var from = new google.maps.LatLng(nodes.from.latitude, nodes.from.longitude);
		var to = new google.maps.LatLng(nodes.to.latitude, nodes.to.longitude);
		
		lines[key].setPath([from, to]);
	};
	
	this.removed = function(key, nodes) {
		lines[key].setMap(null);
	};
}
