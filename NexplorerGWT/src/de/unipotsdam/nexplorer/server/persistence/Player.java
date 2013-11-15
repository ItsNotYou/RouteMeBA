package de.unipotsdam.nexplorer.server.persistence;

import static com.google.common.collect.Collections2.filter;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.unipotsdam.nexplorer.server.PojoAction;
import de.unipotsdam.nexplorer.server.data.NeighbourAction;
import de.unipotsdam.nexplorer.server.data.PlayerDoesNotExistException;
import de.unipotsdam.nexplorer.server.di.InjectLogger;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingTableEntries;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Neighbours;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.shared.Locatable;
import de.unipotsdam.nexplorer.shared.Location;

public class Player implements Locatable {

	/**
	 * Set booster duration to 15 seconds.
	 */
	private static final long BOOSTER_DURATION = 15000;

	/**
	 * Set booster range to 4 meters.
	 */
	private static final long BOOSTER_RANGE = 4;

	@InjectLogger
	private Logger logger;
	private final Players inner;
	private final DatabaseImpl dbAccess;
	private final NeighbourSet neighbours;
	private final DataFactory data;

	@Inject
	public Player(@Assisted Players player, DatabaseImpl dbAccess, DataFactory data) {
		this.inner = player;
		this.dbAccess = dbAccess;
		this.neighbours = data.neighbours(inner);
		this.data = data;
	}

	/**
	 * Gets the nodes sending range. Considers enhancements like signal range boosters.
	 * 
	 * @return Nodes sending range in meters.
	 */
	public long getRange() {
		Long signalRangeBooster = inner.getHasSignalRangeBooster();
		if (isBoosterActive(signalRangeBooster)) {
			return inner.getBaseNodeRange() + BOOSTER_RANGE;
		} else {
			return inner.getBaseNodeRange();
		}
	}

	/**
	 * A booster is active if a booster was activated within the last {@link #BOOSTER_DURATION} ms.
	 * 
	 * @param lastBoosterActivation
	 *            Timestamp of the last booster activation. The timestamp is defined as the value that is returned by {@link Date#getTime()}.
	 * @return <tt>true</tt> if a booster is active, <tt>false</tt> otherwise.
	 */
	private boolean isBoosterActive(Long lastBoosterActivation) {
		if (lastBoosterActivation == null) {
			return false;
		}

		long now = new Date().getTime();
		long then = lastBoosterActivation;

		return (now - then) <= BOOSTER_DURATION;
	}

	public Long incSequenceNumber() {
		Long sequenceNumber = inner.getSequenceNumber();
		sequenceNumber++;
		inner.setSequenceNumber(sequenceNumber);
		return sequenceNumber;
	}

	public Set<Player> getNeighbours() {
		return neighbours;
	}

	public void save() {
		dbAccess.persist(inner);
	}

	public Item getItemInCollectionRange() throws NullPointerException {
		try {
			List<Item> items = dbAccess.getAllItemsNear(new Location(inner.getLatitude(), inner.getLongitude()), inner.getItemCollectionRange());
			return items.isEmpty() ? null : items.get(0);
		} catch (NullPointerException e) {
			throw new PlayerDoesNotExistException("Nullpointer in player get items getItemInCollectionRange");
		}
	}

	public Map<Object, PojoAction> updateNeighbourhood(NeighbourAction routing, List<Neighbour> allKnownNeighbours, long currentRoutingRound, List<AodvRoutingTableEntries> routingTable) {
		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();

		Collection<Player> knownNeighbours = getNeighbours();
		List<Player> reachableNodes = dbAccess.getNeighboursWithinRange(this);

		logger.trace("Node {} aktualisiert seine Nachbarschaft", getId());

		for (Player p : knownNeighbours) {
			logger.trace("Node {} kennt {}", getId(), p.getId());
		}

		for (Player p : reachableNodes) {
			logger.trace("Node {} erreicht {}", getId(), p.getId());
		}

		// Nachbar existiert noch nicht in der Liste
		Collection<Player> newNeighbours = Lists.newArrayList(filter(reachableNodes, isNotIn(knownNeighbours)));
		// Spieler ist nicht mehr Nachbar, wenn er in der Abfrage der aktuellen Nachbarn nicht mehr auftaucht
		Collection<Player> lostNeighbours = Lists.newArrayList(filter(knownNeighbours, isNotIn(reachableNodes)));

		for (Player p : newNeighbours) {
			logger.trace("Node {} hat {} als neu gefunden", getId(), p.getId());
		}

		for (Player p : lostNeighbours) {
			logger.trace("Node {} hat {} als verloren gemeldet", getId(), p.getId());
		}

		for (Player lostNeighbour : lostNeighbours) {
			getNeighbours().remove(lostNeighbour);
			Map<Object, PojoAction> result = routing.aodvNeighbourLost(lostNeighbour, allKnownNeighbours, currentRoutingRound, routingTable);
			persistables.putAll(result);

			logger.info("Node {} deleted neighbour {}", getId(), lostNeighbour.getId());
		}

		for (Player newNeighbour : newNeighbours) {
			getNeighbours().add(newNeighbour);
			Map<Object, PojoAction> result = routing.aodvNeighbourFound(newNeighbour, routingTable);
			persistables.putAll(result);

			logger.trace("Node {} added neighbour {}", getId(), newNeighbour.getId());
		}

		save();
		logger.trace("Node {} hat seine Nachbarschaft aktualisiert", getId());

		return persistables;
	}

	private Predicate<? super Player> isNotIn(Collection<Player> elements) {
		return new IsNotIn(elements);
	}

	public String getName() {
		return inner.getName();
	}

	public boolean hasBattery() {
		return inner.getBattery() > 0;
	}

	@Override
	public double getLatitude() {
		return inner.getLatitude();
	}

	@Override
	public double getLongitude() {
		return inner.getLongitude();
	}

	public Long getId() {
		return inner.getId();
	}

	/**
	 * Returns a sorted list of items visible to this player. The list is sorted by distance between this player and the items (ascending).
	 * 
	 * @return The sorted list
	 */
	public List<Item> getVisibleItems() {
		return dbAccess.getAllItemsNear(this, inner.getItemCollectionRange());
	}

	public void setLocation(Location location) {
		inner.setLatitude(location.getLatitude());
		inner.setLongitude(location.getLongitude());
		inner.setLastPositionUpdate(new Date().getTime());
	}

	public void increaseBatteryBy(double delta) {
		inner.setBattery(Math.min(inner.getBattery() + delta, 100));
	}

	public void activateSignalRangeBooster() {
		inner.setHasSignalRangeBooster(new Date().getTime());
	}

	public Long getScore() {
		return inner.getScore();
	}

	public void decreaseBatteryBy(double delta) {
		logger.trace("Decreasing player battery {} from {} by {}", getId(), inner.getBattery(), delta);

		double current = inner.getBattery();
		current -= delta;
		current = Math.max(0, current);
		inner.setBattery(current);
	}

	public void increaseScoreBy(long delta) {
		inner.setScore(inner.getScore() + delta);
	}

	public void execute(PlayerInternal action) throws PlayerDoesNotExistException {
		action.execute(inner);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != getClass())
			return false;
		Player other = (Player) obj;
		if (inner == other.inner)
			return true;
		if (inner.getId() == null || other.inner.getId() == null)
			return false;
		return inner.getId() == other.inner.getId();
	}

	public Long getDifficulty() {
		return inner.getDifficulty();
	}

	/**
	 * Adds itself as known neighbour to all nodes within range. Does not add the neighbours within range to its own known neighbours. Result: All neighbours know this node, but this node does not know its neighbours.
	 */
	public void pingNeighbourhood() {
		inner.setLastPing(new Date().getTime());
		List<Player> reachableNodes = dbAccess.getNeighboursWithinRange(this);
		for (Player neighbour : reachableNodes) {
			neighbour.receivePingFrom(this);
		}
	}

	public void receivePingFrom(Player player) {
		// Search for existing neighbour connection
		Neighbours existingConnection = null;
		for (Neighbours neigh : inner.getNeighbourses()) {
			if (neigh.getNeighbour().getId() == player.getId()) {
				existingConnection = neigh;
				break;
			}
		}

		if (existingConnection != null) {
			// If connection exists -> update it
			existingConnection.setLastPing(player.inner.getLastPing());
		} else {
			// If no connection exists -> create it
			Neighbours neigh = new Neighbours(player.inner, inner);
			neigh.setLastPing(player.inner.getLastPing());
			inner.getNeighbourses().add(neigh);
		}
	}

	public synchronized Map<Object, PojoAction> removeOutdatedNeighbours(NeighbourAction routing, List<Neighbour> allKnownNeighbours, long currentRoutingRound, List<AodvRoutingTableEntries> routingTable) {
		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();

		int allowedHelloLosses = 8;
		long tooOld = new Date().getTime() - allowedHelloLosses * inner.getPingDuration();

		Set<Neighbours> knownNeighbours = inner.getNeighbourses();
		Iterator<Neighbours> neighIterator = knownNeighbours.iterator();
		while (neighIterator.hasNext()) {
			Neighbours neighbour = neighIterator.next();
			if (neighbour.getLastPing() != null && neighbour.getLastPing() < tooOld) {
				Players inner = neighbour.getNode();

				neighIterator.remove();
				dbAccess.persist(inner);
				dbAccess.delete(neighbour);

				Map<Object, PojoAction> result = routing.aodvNeighbourLost(data.create(neighbour.getNeighbour()), allKnownNeighbours, currentRoutingRound, routingTable);
				persistables.putAll(result);
			}
		}

		return persistables;
	}

	public Collection<AodvDataPackets> getCurrentDataPackets() {
		return Collections.unmodifiableSet(inner.getAodvDataPacketsesForCurrentNodeId());
	}
}
