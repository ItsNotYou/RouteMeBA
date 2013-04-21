package de.unipotsdam.nexplorer.client.indoor;

import de.unipotsdam.nexplorer.client.indoor.levels.Route;
import de.unipotsdam.nexplorer.client.indoor.levels.RouteKeeper;
import de.unipotsdam.nexplorer.client.indoor.view.messaging.RouteClickListener;

public class RouteRemover implements RouteClickListener {

	private final RouteKeeper keeper;

	public RouteRemover(RouteKeeper keeper) {
		this.keeper = keeper;
	}

	@Override
	public void onRouteClick(Route route) {
		keeper.removedUsed(route);
	}

	@Override
	public void onRouteHovered(Route route) {
	}
}
