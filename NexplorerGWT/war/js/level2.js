function RandomRoutes() {

    function factorial(length) {
        if (length <= 0) {
            return 1;
        } else {
            return length * factorial(length - 1);
        }
    }

    /**
     *
     * @param {Object} nodes Object with lots of nodes, identified by their keys
     * @param {number} routeCount Maximum number of created routes. May return less routes if the number of nodes is not sufficient
     * @returns {Array}
     */
    this.createRoutes = function (nodes, routeCount) {
        // Collect keys
        var keys = [];
        for (var key in nodes) {
            keys.push(key);
        }

        // Reduce number of searched routes to request or to mathematical maximum
        var maxFindableRouteCount = factorial(keys.length);
        var maxRouteCount = Math.min(routeCount, maxFindableRouteCount);

        // Result buffer
        var results = [];
        do {
            var route = createRandomRoute(keys);
            if (!this.contains(route, results)) {
                results.push(route);
            }
        } while (results.length < maxRouteCount);

        return results.sort(byKey);
    }

    function byKey(first, second) {
        if (parseInt(first.from) < parseInt(second.from)) {
            return -1;
        } else if (parseInt(first.from) == parseInt(second.from)) {
            return parseInt(first.to) - parseInt(second.to);
        } else {
            return 1;
        }
    }

    /**
     *
     * @param {Array} keys
     * @returns {{from: *, to: *}}
     */
    function createRandomRoute(keys) {
        var firstIndex = Math.floor(Math.random() * keys.length);
        var secondIndex = Math.floor(Math.random() * keys.length);
        var first = keys[firstIndex];
        var second = keys[secondIndex];

        var result = {"from": first, "to": second};
        if (first == second) {
            result = createRandomRoute(keys);
        }
        return result;
    }

    /**
     * Tests if an element within the given array equals the given element based on the "from" and "to" key
     * @param {{from: *, to: *}} element Object with "from" and "to" key
     * @param {{from: *, to: *}[]} array Array to search in
     * @returns {boolean}
     */
    this.contains = function (element, array) {
        for (var key in array) {
            var from = array[key].from;
            var to = array[key].to;

            if (element.from == from && element.to == to) {
                return true;
            }
        }
        return false;
    }
}
