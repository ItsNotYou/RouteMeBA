package de.unipotsdam.nexplorer.client.indoor.levels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RouteKeeper {

	private final List<Route> viewedRoutes;
	private final List<RouteListener> listeners;
	private List<Node> availableNodes;
	private final Random random;
	private int routeCount;

	public RouteKeeper() {
		this.viewedRoutes = new ArrayList<Route>();
		this.listeners = new ArrayList<RouteListener>();
		this.availableNodes = new ArrayList<Node>();
		this.random = new Random();
	}

	public void removedUsed(Route route) {
		viewedRoutes.remove(route);
		updateListeners();
	}

	public void addRouteListener(RouteListener listener) {
		listeners.add(listener);
	}

	private void updateListeners() {
		for (RouteListener listener : listeners) {
			listener.updateRoutes(viewedRoutes);
		}
	}

	public void setRouteCount(int routeCount) {
		this.routeCount = routeCount;
	}

	public void updateAvailableNodes(List<Node> nodes) {
		this.availableNodes = nodes;
		updateRoutes();
	}

	private void updateRoutes() {
		removeUnavailableRoutes(viewedRoutes, availableNodes);
		fillWithAvailableNodes(viewedRoutes, availableNodes);
		updateListeners();
	}

	private long maxRouteCount(long length) {
		return length * (length - 1);
	}

	private void fillWithAvailableNodes(List<Route> current, List<Node> available) {
		long max = maxRouteCount(available.size());
		max = Math.min(max, routeCount);
		while (current.size() < max) {
			addRandomRoute(current, available);
		}
	}

	private void addRandomRoute(List<Route> current, List<Node> available) {
		Route route = null;
		do {
			route = createRandomRoute(available);
		} while (current.contains(route));
		current.add(route);
	}

	private void removeUnavailableRoutes(List<Route> current, List<Node> available) {
		Iterator<Route> it = current.iterator();
		while (it.hasNext()) {
			Route disputable = it.next();
			if (isUnavailable(disputable.getSource(), available) || isUnavailable(disputable.getDestination(), available)) {
				it.remove();
			}
		}
	}

	private boolean isUnavailable(String id, List<Node> available) {
		for (Node check : available) {
			if (check.getId().equals(id)) {
				return false;
			}
		}
		return true;
	}

	private Route createRandomRoute(List<Node> nodes) {
		int source = random.nextInt(nodes.size());
		int dest = random.nextInt(nodes.size());

		Route result = new Route(nodes.get(source).getId(), nodes.get(dest).getId());
		if (result.getSource().equals(result.getDestination())) {
			result = createRandomRoute(nodes);
		}
		return result;
	}
}
