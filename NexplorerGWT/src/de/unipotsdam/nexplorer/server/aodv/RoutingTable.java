package de.unipotsdam.nexplorer.server.aodv;

import java.util.List;

import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;

public class RoutingTable {

	private AodvNode node;
	private DatabaseImpl dbAccess;

	public RoutingTable(AodvNode player, DatabaseImpl dbAccess) {
		this.node = player;
		this.dbAccess = dbAccess;
	}

	public boolean hasRouteTo(AodvNode destination) {
		return hasRouteTo(destination.getId());
	}

	public boolean hasRouteTo(Player destination) {
		return hasRouteTo(destination.getId());
	}

	public boolean hasRouteTo(long destination) {
		AodvRoutingTableEntry theRoute = dbAccess.getRouteToDestination(destination, node.getId());
		return theRoute != null;
	}

	public long getHopCountTo(AodvNode destination) {
		if (destination.getId() == node.getId()) {
			return 0;
		} else {
			AodvRoutingTableEntry theRoute = dbAccess.getRouteToDestination(destination.getId(), node.getId());
			return theRoute.getHopCount();
		}
	}

	public AodvNode getNextHop(AodvNode destination) {
		return getNextHop(destination.getId());
	}

	public AodvNode getNextHop(long destination) {
		AodvRoutingTableEntry theRoute = dbAccess.getRouteToDestination(destination, node.getId());
		return theRoute.getNextHop();
	}

	public void deleteRouteTo(AodvNode destination) {
		deleteRouteTo(destination.getId());
	}

	public void deleteRouteTo(long destination) {
		dbAccess.removeRoutingEntries(node.getId(), destination);
		AodvRoutingTableEntry theRoute = dbAccess.getRouteToDestination(destination, node.getId());
		if (theRoute != null) {
			theRoute.delete();
		}
	}

	public void add(Route route) {
		route.persist(node.getId(), dbAccess);
	}

	public void addRoute(long nextHop, long dest, long hopCount, long sequenceNumber) {
		boolean useRoute = false;
		// alte Routen zum Ziel betrachten
		List<AodvRoutingTableEntry> oldRoutes = dbAccess.getRoutingTableEntries(node.getId(), dest);
		if (oldRoutes.isEmpty()) {
			System.out.println("Keine alten Routen für Knoten " + node.getId() + " gefunden. Neue Route zu Knoten " + dest + " wurde eingetragen.\n");
			add(new Route(nextHop, dest, sequenceNumber, hopCount));
		} else {
			for (AodvRoutingTableEntry theOldRoute : oldRoutes) {
				if (theOldRoute.getDestinationSequenceNumber() < sequenceNumber) {
					System.out.println("Lösche veraltete Route (alte Seq " + theOldRoute.getDestinationSequenceNumber() + " < neue Seq " + sequenceNumber + ") von Knoten " + node.getId() + " zu Knoten " + dest + ".\n");

					theOldRoute.delete();
					useRoute = true;
				}
			}

			if (useRoute) {
				System.out.println("Neue Route von Knoten " + node.getId() + " zu Knoten " + dest + " wurde eingetragen.\n");
				add(new Route(nextHop, dest, sequenceNumber, hopCount));
			}
		}
	}
}
