package de.unipotsdam.nexplorer.server.data;

import de.unipotsdam.nexplorer.server.aodv.Route;
import de.unipotsdam.nexplorer.server.persistence.Player;

public class NeighbourRoute extends Route {

	public NeighbourRoute(Player dest) {
		super(dest.getId(), dest.getId(), dest.incSequenceNumber(), 1l);
	}
}
