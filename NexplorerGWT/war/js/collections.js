function UpdatableCollection() {
	
	// Always save the current object
	this.current = {};
	
	this.update = function(updatedElements) {
		var newKeys = [];
		var oldKeys = [];
		var sameKeys = [];
		
		// Determine new and same keys
		for (key in updatedElements) {
			if (this.current[key] == undefined) {
				// Anything that cannot be found in the current elements must be new
				newKeys.push(key);
			} else {
				// Everything that exists in the current and the updated elements must be same (in terms of its key)
				sameKeys.push(key);
			}
		}
		
		// Determine old keys
		for (key in this.current) {
			if (updatedElements[key] == undefined) {
				// Anything that cannot be found in the updated elements must be old
				oldKeys.push(key);
			}
		}
		
		this.current = updatedElements;
		return {newKeys:newKeys, sameKeys:sameKeys, oldKeys:oldKeys};
	}
}

function ObservableCollection() {
	
	var collection = new UpdatableCollection();
	var subscribers = [];
	
	this.subscribe = function(observer) {
		subscribers.push(observer);
	}
	
	this.update = function(updatedElements) {
		var current = collection.current;
		var diff = collection.update(updatedElements);
		
		for (key in subscribers) {
			var subscriber = subscribers[key];
			call(diff.newKeys, updatedElements, subscriber.added);
			call(diff.sameKeys, updatedElements, subscriber.remained);
			call(diff.oldKeys, current, subscriber.removed);
		}
	}
	
	function call(collection, values, callback) {
		if (callback != undefined) {
			for (key in collection) {
				callback(collection[key], values[collection[key]]);
			}
		}
	}
}
