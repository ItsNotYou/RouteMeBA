package de.unipotsdam.nexplorer.server.aodv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
	private List<AodvRoutingTableEntries> allRoutingTableEntries;

	public RoutingTable(AodvNode player, DatabaseImpl dbAccess) {
		this.node = player;
		this.dbAccess = dbAccess;

		this.allRoutingTableEntries = dbAccess.getAllRoutingTableEntries();
	}

	private AodvRoutingTableEntries getRouteToDestination(final Long destinationId, final Long nodeId) {
		Collection<AodvRoutingTableEntries> filtered = Collections2.filter(allRoutingTableEntries, new Predicate<AodvRoutingTableEntries>() {

			@Override
			public boolean apply(AodvRoutingTableEntries arg0) {
				if (arg0.getNodeId() != nodeId) {
					return false;
				}
				if (arg0.getDestinationId() != destinationId) {
					return false;
				}
				return true;
			}
		});

		List<AodvRoutingTableEntries> sortable = new ArrayList<AodvRoutingTableEntries>(filtered);
		Collections.sort(sortable, new Comparator<AodvRoutingTableEntries>() {

			@Override
			public int compare(AodvRoutingTableEntries o1, AodvRoutingTableEntries o2) {
				if (o1 == null && o2 == null) {
					return 0;
				} else if (o1 == null && o2 != null) {
					return -1;
				} else if (o1 != null && o2 == null) {
					return 1;
				}

				Long hop1 = o1.getHopCount();
				Long hop2 = o2.getHopCount();

				if (hop1 == null && hop2 == null) {
					return 0;
				} else if (hop1 == null && hop2 != null) {
					return -1;
				} else if (hop1 != null && hop2 == null) {
					return 1;
				}

				return (int) (hop1 - hop2);
			}
		});

		return sortable.isEmpty() ? null : sortable.get(0);
	}

	public boolean hasRouteTo(AodvNode destination) {
		return hasRouteTo(destination.getId());
	}

	public boolean hasRouteTo(Player destination) {
		return hasRouteTo(destination.getId());
	}

	public boolean hasRouteTo(long destination) {
		AodvRoutingTableEntries theRoute = getRouteToDestination(destination, node.getId());
		return theRoute != null;
	}

	public long getHopCountTo(AodvNode destination) {
		return getHopCountTo(destination.getId());
	}

	public long getHopCountTo(long destination) {
		if (destination == node.getId()) {
			return 0;
		} else {
			AodvRoutingTableEntries theRoute = getRouteToDestination(destination, node.getId());
			return theRoute.getHopCount();
		}
	}

	public long getNextHop(AodvNode destination) {
		return getNextHop(destination.getId());
	}

	public long getNextHop(long destination) {
		AodvRoutingTableEntries theRoute = getRouteToDestination(destination, node.getId());
		return theRoute.getNextHopId();
	}

	public Map<Object, PojoAction> deleteRouteTo(long destination) {
		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();

		dbAccess.removeRoutingEntries(node.getId(), destination);
		AodvRoutingTableEntries theRoute = getRouteToDestination(destination, node.getId());
		if (theRoute != null) {
			persistables.put(theRoute, PojoAction.DELETE);
		}

		return persistables;
	}

	public Map<Object, PojoAction> add(Route route) {
		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();

		Collection<Object> result = route.persist(node.getId());
		for (Object persistable : result) {
			persistables.put(persistable, PojoAction.SAVE);
		}

		return persistables;
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
