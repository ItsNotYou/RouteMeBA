package de.unipotsdam.nexplorer.client.indoor.levels;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouteKeeper {

	private final List<Route> viewedRoutes;
	private final List<RouteListener> listeners;
	private List<Node> availableNodes;
	private final Random random;

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
	}

	public void updateAvailableNodes(List<Node> nodes) {
		this.availableNodes = nodes;
		updateRoutes();
	}

	private void updateRoutes() {
		viewedRoutes.clear();
		for (Node source : availableNodes) {
			for (Node dest : availableNodes) {
				if (source.getId().equals(dest.getId())) {
					continue;
				}

				viewedRoutes.add(new Route(source.getId(), dest.getId()));
			}
		}

		updateListeners();
	}
}
