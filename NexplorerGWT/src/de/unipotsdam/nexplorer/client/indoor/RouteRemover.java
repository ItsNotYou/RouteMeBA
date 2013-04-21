package de.unipotsdam.nexplorer.client.indoor;

import de.unipotsdam.nexplorer.client.indoor.levels.Route;
import de.unipotsdam.nexplorer.client.indoor.levels.RouteKeeper;
import de.unipotsdam.nexplorer.client.indoor.viewcontroller.ButtonSetShown;
import de.unipotsdam.nexplorer.shared.DataPacket;

public class RouteRemover implements StateSwitchListener {

	private final RouteKeeper keeper;

	public RouteRemover(RouteKeeper keeper) {
		this.keeper = keeper;
	}

	@Override
	public void stateSwitchedTo(ButtonSetShown state, DataPacket reason) {
		long source = reason.getMessageDescription().getSourceNodeId();
		long destination = reason.getMessageDescription().getDestinationNodeId();

		Route usedRoute = new Route(Long.toString(source), Long.toString(destination));
		keeper.removedUsed(usedRoute);
	}
}
