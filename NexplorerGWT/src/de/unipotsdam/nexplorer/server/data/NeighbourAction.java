package de.unipotsdam.nexplorer.server.data;

import java.util.List;

import de.unipotsdam.nexplorer.server.persistence.Neighbour;
import de.unipotsdam.nexplorer.server.persistence.Player;

public interface NeighbourAction {

	/**
	 * Notifies listeners of a neighbour loss
	 * 
	 * @param exNeighbour
	 *            The lost neighbour
	 * @param allKnownNeighbours
	 *            All currently known neighbours. The recently lost <code>exNeighbour</code> may be part of the list.
	 */
	void aodvNeighbourLost(Player exNeighbour, List<Neighbour> allKnownNeighbours);

	void aodvNeighbourFound(Player thePlayer);
}
