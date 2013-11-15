package de.unipotsdam.nexplorer.server.data;

import java.util.List;
import java.util.Map;

import de.unipotsdam.nexplorer.server.PojoAction;
import de.unipotsdam.nexplorer.server.persistence.Neighbour;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingTableEntries;

public interface NeighbourAction {

	/**
	 * Notifies listeners of a neighbour loss
	 * 
	 * @param exNeighbour
	 *            The lost neighbour
	 * @param allKnownNeighbours
	 *            All currently known neighbours. The recently lost <code>exNeighbour</code> may be part of the list.
	 * @param currentRoutingRound
	 *            The currently executing round of message routing (not data routing).
	 */
	Map<Object, PojoAction> aodvNeighbourLost(Player exNeighbour, List<Neighbour> allKnownNeighbours, long currentRoutingRound, List<AodvRoutingTableEntries> routingTable);

	Map<Object, PojoAction> aodvNeighbourFound(Player thePlayer, List<AodvRoutingTableEntries> routingTable);
}
