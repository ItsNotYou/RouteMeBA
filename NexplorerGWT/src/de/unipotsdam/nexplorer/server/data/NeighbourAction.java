package de.unipotsdam.nexplorer.server.data;

import de.unipotsdam.nexplorer.server.persistence.Player;

public interface NeighbourAction {

	void aodvNeighbourLost(Player exNeighbour);

	void aodvNeighbourFound(Player thePlayer);
}
