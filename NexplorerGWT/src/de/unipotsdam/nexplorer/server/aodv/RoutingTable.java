package de.unipotsdam.nexplorer.server.aodv;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import de.unipotsdam.nexplorer.server.PojoAction;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingTableEntries;

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
		return getHopCountTo(destination.getId());
	}

	public long getHopCountTo(long destination) {
		if (destination == node.getId()) {
			return 0;
		} else {
			AodvRoutingTableEntry theRoute = dbAccess.getRouteToDestination(destination, node.getId());
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
		Collection<Object> persistables = route.persist(node.getId());
		for (Object persistable : persistables) {
			dbAccess.persistObject(persistable);
		}
	}

	private static Map<Object, PojoAction> add(Route route, long src) {
		Map<Object, PojoAction> result = new HashMap<Object, PojoAction>();
		Collection<Object> persistables = route.persist(src);
		for (Object persistable : persistables) {
			result.put(persistable, PojoAction.SAVE);
		}
		return result;
	}

	public static Map<Object, PojoAction> addRoute(long src, long nextHop, long dest, long hopCount, long sequenceNumber, List<AodvRoutingTableEntries> allRoutingTableEntries) {
		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();

		boolean useRoute = false;
		// alte Routen zum Ziel betrachten
		Collection<AodvRoutingTableEntries> oldRoutes = getRoutingTableEntries(src, dest, allRoutingTableEntries);
		if (oldRoutes.isEmpty()) {
			System.out.println("Keine alten Routen für Knoten " + src + " gefunden. Neue Route zu Knoten " + dest + " wurde eingetragen.\n");
			Map<Object, PojoAction> result = add(new Route(nextHop, dest, sequenceNumber, hopCount), src);
			persistables.putAll(result);
		} else {
			for (AodvRoutingTableEntries theOldRoute : oldRoutes) {
				if (theOldRoute.getDestinationSequenceNumber() < sequenceNumber) {
					System.out.println("Lösche veraltete Route (alte Seq " + theOldRoute.getDestinationSequenceNumber() + " < neue Seq " + sequenceNumber + ") von Knoten " + src + " zu Knoten " + dest + ".\n");

					persistables.put(theOldRoute, PojoAction.DELETE);
					useRoute = true;
				}
			}

			if (useRoute) {
				System.out.println("Neue Route von Knoten " + src + " zu Knoten " + dest + " wurde eingetragen.\n");
				Map<Object, PojoAction> result = add(new Route(nextHop, dest, sequenceNumber, hopCount), src);
				persistables.putAll(result);
			}
		}

		return persistables;
	}

	private static Collection<AodvRoutingTableEntries> getRoutingTableEntries(final long src, final long dest, List<AodvRoutingTableEntries> allRoutingTableEntries) {
		return Collections2.filter(allRoutingTableEntries, new Predicate<AodvRoutingTableEntries>() {

			@Override
			public boolean apply(AodvRoutingTableEntries entry) {
				if (entry.getNodeId() != src) {
					return false;
				}
				if (entry.getDestinationId() != dest) {
					return false;
				}

				return true;
			}
		});
	}
}
