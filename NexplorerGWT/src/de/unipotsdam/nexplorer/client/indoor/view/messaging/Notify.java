package de.unipotsdam.nexplorer.client.indoor.view.messaging;

import java.util.Collection;

import de.unipotsdam.nexplorer.client.indoor.levels.Route;

public class Notify implements RouteClickListener {

	private final Collection<RouteClickListener> observers;

	public Notify(Collection<RouteClickListener> clickObservers) {
		this.observers = clickObservers;
	}

	@Override
	public void onRouteClick(Route route) {
		for (RouteClickListener listener : observers) {
			listener.onRouteClick(route);
		}
	}
}
