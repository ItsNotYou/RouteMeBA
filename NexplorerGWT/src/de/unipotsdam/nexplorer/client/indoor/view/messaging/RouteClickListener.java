package de.unipotsdam.nexplorer.client.indoor.view.messaging;

import de.unipotsdam.nexplorer.client.indoor.levels.Route;

public interface RouteClickListener {

	public void onRouteClick(Route route);

	public void onRouteHovered(Route route);
}
