function NeighbourObserver() {
	
	var current = {};
	var subscribers = [];
	
	this.subscribe = function(observer) {
		subscribers.push(observer);
	}
	
	this.added = function(key, node) {
		var keysValues = mapAllNeighbours(key, node);
		for (i in subscribers) {
			calls(keysValues, subscribers[i].added)
		}
		
		current[key] = new UpdatableCollection();
		current[key].update(map(keysValues));
	};
	
	this.remained = function(key, node) {
		var keysValues = map(mapAllNeighbours(key, node));
		var currentKeysValues = current[key].current;
		var diff = current[key].update(keysValues);
		
		for (i in subscribers) {
			var subscriber = subscribers[i];
			call(diff.newKeys, keysValues, subscriber.added);
			call(diff.sameKeys, keysValues, subscriber.remained);
			call(diff.oldKeys, currentKeysValues, subscriber.removed);
		}
	};
	
	this.removed = function(key, node) {
		var keysValues = mapAllNeighbours(key, node);
		for (i in subscribers) {
			calls(keysValues, subscribers[i].removed)
		}
	};
	
	function mapAllNeighbours(key, node) {
		var keysValues = [];
		for (neighbour in node.neighbours) {
			var addKey = key + "-" + neighbour;
			var addValue = {from:node,to:node.neighbours[neighbour]};
			keysValues.push({key:addKey, value:addValue});
		}
		return keysValues;
	}
	
	function calls(keysValues, callback) {
		if (callback != undefined) {
			for (i in keysValues) {
				var key = keysValues[i].key;
				var value = keysValues[i].value;
				callback(key, value);
			}
		}
	}
	
	function call(collection, values, callback) {
		if (callback != undefined) {
			for (key in collection) {
				callback(collection[key], values[collection[key]]);
			}
		}
	}
	
	function map(collection) {
		var result = {};
		for (i in collection) {
			var key = collection[i].key;
			var value = collection[i].value;
			result[key] = value;
		}
		return result;
	}
}
