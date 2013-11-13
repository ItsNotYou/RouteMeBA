package de.unipotsdam.nexplorer.server.persistence;

import static org.hibernate.criterion.Order.asc;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.gt;
import static org.hibernate.criterion.Restrictions.not;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import com.google.inject.Inject;

import de.unipotsdam.nexplorer.server.aodv.AodvDataPacket;
import de.unipotsdam.nexplorer.server.aodv.AodvFactory;
import de.unipotsdam.nexplorer.server.aodv.AodvRouteRequestBufferEntry;
import de.unipotsdam.nexplorer.server.aodv.AodvRoutingMessage;
import de.unipotsdam.nexplorer.server.aodv.AodvRoutingTableEntry;
import de.unipotsdam.nexplorer.server.aodv.Locator;
import de.unipotsdam.nexplorer.server.data.PlayerDoesNotExistException;
import de.unipotsdam.nexplorer.server.di.InjectLogger;
import de.unipotsdam.nexplorer.server.persistence.hibernate.TruncateTables;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRouteRequestBufferEntries;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingMessages;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingTableEntries;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Neighbours;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.PositionBacklog;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;
import de.unipotsdam.nexplorer.shared.Aodv;
import de.unipotsdam.nexplorer.shared.DataPacket;
import de.unipotsdam.nexplorer.shared.ItemType;
import de.unipotsdam.nexplorer.shared.Locatable;
import de.unipotsdam.nexplorer.shared.Messager;

public class DatabaseImpl {

	@InjectLogger
	private Logger logger;
	private final Session session;
	private final AodvFactory factory;
	private final Locator locator;
	private final ScoreComparator byScore;
	private final DataFactory data;

	@Inject
	public DatabaseImpl(Session session, AodvFactory factory, Locator locator, ScoreComparator byScore, DataFactory data) {
		this.session = session;
		this.factory = factory;
		this.locator = locator;
		this.byScore = byScore;
		this.data = data;
	}

	public Players getRawById(long id) {
		return (Players) session.createCriteria(Players.class).add(eq("id", id)).uniqueResult();
	}

	public List<Player> getAllActiveNodesInRandomOrder() {
		Criteria query = session.createCriteria(Players.class);
		query.add(eq("role", Role.NODE)).add(gt("battery", 0.));
		query.setFetchMode("neighbourses", FetchMode.JOIN);
		List<Players> players = query.list();

		List<Player> result = new LinkedList<Player>();
		for (Players player : players) {
			result.add(data.create(player));
		}

		Collections.shuffle(result);
		return result;
	}

	public List<AodvDataPacket> getAllDataPacketsSortedByDate(Player theNode) {
		Setting settings = getSettings();

		List<AodvDataPackets> dataPackets = session.createCriteria(AodvDataPackets.class).add(not(eq("status", Aodv.DATA_PACKET_STATUS_ARRIVED))).add(not(eq("status", Aodv.DATA_PACKET_STATUS_CANCELLED))).add(eq("processingRound", settings.getCurrentDataRound())).addOrder(Order.asc("id")).createCriteria("playersByCurrentNodeId").add(eq("id", theNode.getId())).list();

		List<AodvDataPacket> result = new LinkedList<AodvDataPacket>();
		for (AodvDataPackets packet : dataPackets) {
			result.add(factory.create(packet));
		}
		return result;
	}

	public int getRouteRequestCount(AodvDataPackets thePacket) {
		List<AodvRoutingMessages> messages = session.createCriteria(AodvRoutingMessages.class).add(eq("type", Aodv.ROUTING_MESSAGE_TYPE_RREQ)).add(eq("sourceId", thePacket.getPlayersBySourceId().getId())).add(eq("destinationId", thePacket.getPlayersByDestinationId().getId())).list();
		return messages.size();
	}

	public Setting getSettings() {
		Settings settings = (Settings) session.createCriteria(Settings.class).uniqueResult();
		if (settings == null) {
			return null;
		}

		return new Setting(settings, this);
	}

	public List<AodvRoutingMessage> getRouteRequestsByNodeAndRound(Player theNode) {
		Setting gameSettings = getSettings();
		List<AodvRoutingMessages> messages = session.createCriteria(AodvRoutingMessages.class).add(eq("currentNodeId", theNode.getId())).add(eq("type", Aodv.ROUTING_MESSAGE_TYPE_RREQ)).add(eq("processingRound", gameSettings.getCurrentRoutingRound())).list();

		List<AodvRoutingMessage> result = new LinkedList<AodvRoutingMessage>();
		for (AodvRoutingMessages message : messages) {
			result.add(factory.create(message));
		}
		return result;
	}

	public AodvRouteRequestBufferEntry getAODVRouteRequestBufferEntry(Player theNode, AodvRoutingMessage theRREQ) {
		List<AodvRouteRequestBufferEntries> entries = session.createCriteria(AodvRouteRequestBufferEntries.class).add(eq("nodeId", theNode.getId())).add(eq("sourceId", theRREQ.inner().getSourceId())).add(eq("sequenceNumber", theRREQ.inner().getSequenceNumber())).list();

		return entries.isEmpty() ? null : factory.create(entries.get(0));
	}

	public List<AodvRouteRequestBufferEntries> getAllRouteRequestBufferEntries() {
		return session.createCriteria(AodvRouteRequestBufferEntries.class).list();
	}

	public AodvRoutingTableEntry getRouteToDestination(Long destinationId, Long nodeId) {
		List<AodvRoutingTableEntries> entries = session.createCriteria(AodvRoutingTableEntries.class).add(eq("nodeId", nodeId)).add(eq("destinationId", destinationId)).addOrder(Order.asc("hopCount")).list();

		return entries.isEmpty() ? null : factory.create(entries.get(0));
	}

	public List<AodvRoutingMessage> getRoutingErrors(Player theNode) {
		Setting gameSettings = getSettings();
		List<AodvRoutingMessages> messages = session.createCriteria(AodvRoutingMessages.class).add(eq("currentNodeId", theNode.getId())).add(eq("type", Aodv.ROUTING_MESSAGE_TYPE_RERR)).add(eq("processingRound", gameSettings.getCurrentRoutingRound())).list();

		List<AodvRoutingMessage> result = new LinkedList<AodvRoutingMessage>();
		for (AodvRoutingMessages message : messages) {
			result.add(factory.create(message));
		}
		return result;
	}

	public List<Item> getAllItems() {
		List<Items> items = session.createCriteria(Items.class).list();

		List<Item> result = new LinkedList<Item>();
		for (Items item : items) {
			result.add(data.create(item));
		}
		return result;
	}

	public List<Messager> getAllIndoors() {
		List<Players> indoors = session.createCriteria(Players.class).add(eq("role", Role.MESSAGE)).list();

		List<Messager> result = new LinkedList<Messager>();
		for (Players player : indoors) {
			Messager messager = new Messager();
			messager.id = (player.getId());
			messager.name = player.getName();
			messager.score = (player.getScore());
			result.add(messager);
		}

		return result;
	}

	public List<Player> getAllNodesByScoreDescending() {
		List<Player> nodes = new LinkedList<Player>(this.getAllActiveNodesInRandomOrder());
		Collections.sort(nodes, byScore);
		return nodes;
	}

	public Player getPlayerById(long playerId) {
		List<Players> players = session.createCriteria(Players.class).add(eq("id", playerId)).list();
		if (players.isEmpty()) {
			throw new PlayerDoesNotExistException();
		} else {
			return data.create(players.get(0));
		}
	}

	public List<Item> getAllBatteries() {
		List<Items> items = session.createCriteria(Items.class).add(eq("itemType", ItemType.BATTERY)).list();

		List<Item> result = new LinkedList<Item>();
		for (Items item : items) {
			result.add(data.create(item));
		}
		return result;
	}

	public List<Item> getAllBoosters() {
		List<Items> items = session.createCriteria(Items.class).add(eq("itemType", ItemType.BOOSTER)).list();

		List<Item> result = new LinkedList<Item>();
		for (Items item : items) {
			result.add(data.create(item));
		}
		return result;
	}

	public void resetDatabase() {
		session.doWork(new TruncateTables());
	}

	public void persist(Players data) {
		if (data.getId() == null)
			logger.info("Creating player {}", data.getId(), data.getName());
		try {
			persistObject(data);
		} catch (StackOverflowError e) {
			logger.error("Stackoverflow while persisting Players", e);
		}
	}

	public void persist(DataPacket data) {
		persistObject(data);
	}

	public void persistObject(Object data) {
		session.saveOrUpdate(data);
	}

	public void persist(Neighbours data) {
		// logger.debug("Persisting neighbour of {}: {}", data.getNodeId(),
		// data.getPlayers().getId());

		persistObject(data);
	}

	public void persist(Items data) {
		try {
			persistObject(data);
		} catch (StackOverflowError e) {
			logger.error("Stackoverflow while persisting items", e);
		}
	}

	public void persist(AodvRoutingMessages data) {
		persistObject(data);
	}

	public void persist(Settings data) {
		persistObject(data);
	}

	public void persist(AodvRoutingTableEntries data) {
		if (data.getId() == null)
			logger.trace("Creating route from {} over {} to {}", data.getNodeId(), data.getNextHopId(), data.getDestinationId());

		persistObject(data);
	}

	public void cleanRoutingEntriesFor(Player theNode) {
		List<AodvDataPackets> packets = session.createCriteria(AodvDataPackets.class).createCriteria("playersByCurrentNodeId").add(eq("id", theNode.getId())).list();
		for (DataPacket packet : packets) {
			session.delete(packet);
		}

		List<AodvRoutingMessages> messages = session.createCriteria(AodvRoutingMessages.class).add(eq("currentNodeId", theNode.getId())).list();
		for (AodvRoutingMessages message : messages) {
			session.delete(message);
		}

		List<AodvRouteRequestBufferEntries> bEntries = session.createCriteria(AodvRouteRequestBufferEntries.class).add(eq("nodeId", theNode.getId())).list();
		for (AodvRouteRequestBufferEntries entry : bEntries) {
			session.delete(entry);
		}

		List<AodvRoutingTableEntries> tEntries = session.createCriteria(AodvRoutingTableEntries.class).add(eq("nodeId", theNode.getId())).list();
		for (AodvRoutingTableEntries entry : tEntries) {
			session.delete(entry);
		}
	}

	public AodvDataPacket getDataPacketByOwnerId(Player player) {
		Criteria query = session.createCriteria(AodvDataPackets.class).addOrder(Order.desc("id")).setMaxResults(1);
		query.setFetchMode("playersByCurrentNodeId", FetchMode.JOIN);
		query.setFetchMode("playersBySourceId", FetchMode.JOIN);
		query.setFetchMode("playersByDestinationId", FetchMode.JOIN);
		query.createCriteria("playersByOwnerId").add(eq("id", player.getId()));

		AodvDataPackets packets = (AodvDataPackets) query.uniqueResult();
		return packets == null ? null : factory.create(packets);
	}

	private AodvRoutingTableEntry getRoutingTableEntry(long source, long destination) {
		List<AodvRoutingTableEntries> entries = session.createCriteria(AodvRoutingTableEntries.class).add(eq("nodeId", source)).add(eq("destinationId", destination)).addOrder(asc("hopCount")).list();
		return entries.isEmpty() ? null : factory.create(entries.get(0));
	}

	/**
	 * Returns all neighbours of a center player except the given
	 * 
	 * @deprecated Use {@link #getAllNeighbours(Player)} and filter the {@code except} parameter yourself
	 * @param except
	 *            The player to be filtered out
	 * @param center
	 *            The player which neighbours should be retreived
	 * @return
	 */
	public List<Neighbour> getAllNeighboursExcept(Player except, Player center) {
		List<Neighbours> neighbours = session.createCriteria(Neighbours.class).createAlias("node", "n").createAlias("neighbour", "neigh").add(eq("n.id", center.getId())).add(not(eq("neigh.id", except.getId()))).list();
		List<Neighbour> result = new LinkedList<Neighbour>();
		for (Neighbours neighbour : neighbours) {
			result.add(data.create(neighbour));
		}
		return result;
	}

	/**
	 * Returns all neighbours of a center player. Same as {@link #getAllNeighboursExcept(Player, Player)} but without the filter
	 * 
	 * @param center
	 *            The player which neighbours should be retreived
	 * @return
	 */
	public List<Neighbour> getAllNeighbours(Player center) {
		List<Neighbours> neighbours = session.createCriteria(Neighbours.class).createAlias("node", "n").createAlias("neighbour", "neigh").add(eq("n.id", center.getId())).list();
		List<Neighbour> result = new LinkedList<Neighbour>();
		for (Neighbours neighbour : neighbours) {
			result.add(data.create(neighbour));
		}
		return result;
	}

	public List<AodvRoutingTableEntry> getRoutingTableEntries(long nodeId, Long destinationId) {
		// Doctrine_Query::create().from("AODVRoutingTableEntry").where("nodeId = ? AND destinationId = ?",
		// array(theNodeId, theRequest.destinationId)).execute();
		List<AodvRoutingTableEntries> entries = session.createCriteria(AodvRoutingTableEntries.class).add(eq("nodeId", nodeId)).add(eq("destinationId", destinationId)).list();
		List<AodvRoutingTableEntry> result = new LinkedList<AodvRoutingTableEntry>();
		for (AodvRoutingTableEntries entry : entries) {
			result.add(factory.create(entry));
		}
		return result;
	}

	public List<AodvRoutingTableEntries> getAllRoutingTableEntries() {
		return session.createCriteria(AodvRoutingTableEntries.class).list();
	}

	public List<Player> getNeighboursWithinRange(Player center) {
		// query.having("distance <= ".gameSettings.baseNodeRange / 1000);
		// //FIMI ? warum baseNodeRange durch 1000
		// query.orderBy("distance");
		List<Players> players = session.createCriteria(Players.class).add(not(eq("id", center.getId()))).add(eq("role", Role.NODE)).add(gt("battery", 0.)).list();

		List<Player> result = new LinkedList<Player>();
		for (Players player : players) {
			result.add(data.create(player));
		}

		DistanceComparator byDistance = new DistanceComparator(center, locator);
		Collections.sort(result, byDistance);
		filterByDistanceTo(center, result);

		return result;
	}

	private void filterByDistanceTo(Player center, List<Player> players) {
		Iterator<Player> it = players.iterator();
		while (it.hasNext()) {
			Player p = it.next();
			if (!locator.isInRange(center, p)) {
				it.remove();
			}
		}
	}

	public void removeRoutingEntries(Long playerId, Long nextHop) {
		List<AodvRoutingTableEntries> entries = session.createCriteria(AodvRoutingTableEntries.class).add(eq("nodeId", playerId)).add(eq("nextHopId", nextHop)).list();
		for (AodvRoutingTableEntries entry : entries) {
			delete(entry);
		}
	}

	public void delete(Items item) {
		deleteObject(item);
	}

	public void delete(AodvRoutingTableEntries entry) {
		logger.info("Deleting route from {} over {} to {}", entry.getNodeId(), entry.getNextHopId(), entry.getDestinationId());
		deleteObject(entry);
	}

	public Neighbour getNeighbour(Long neighbourId, Long centerId) {
		List<Neighbours> result = session.createCriteria(Neighbours.class).createAlias("node", "n").createAlias("neighbour", "neigh").add(eq("n.id", centerId)).add(eq("neigh.id", neighbourId)).list();
		return result.isEmpty() ? null : data.create(result.get(0));
	}

	public List<Item> getAllItemsNear(Locatable location, double itemCollectionRange) {
		List<Items> items = session.createCriteria(Items.class).list();

		// Filter by distance
		List<Items> nearItems = new LinkedList<Items>();
		for (Items item : items) {
			if (locator.distance(location, item) <= itemCollectionRange / 1000) {
				nearItems.add(item);
			}
		}

		// Order by distance
		ItemDistanceComparator byDistance = new ItemDistanceComparator(location, locator);
		Collections.sort(nearItems, byDistance);

		// Return result
		List<Item> result = new LinkedList<Item>();
		for (Items item : nearItems) {
			result.add(data.create(item));
		}

		return result;
	}

	public void deleteObject(Object data) {
		// session.flush();
		session.delete(data);
	}

	public void delete(AodvRoutingMessages data) {
		deleteObject(data);
	}

	public void delete(DataPacket inner) {
		deleteObject(inner);
	}

	public void delete(Neighbours inner) {
		deleteObject(inner);
	}

	public void persist(AodvRouteRequestBufferEntries newBufferEntry) {
		deleteObject(newBufferEntry);
	}

	public Player getPlayerByIdAndRole(String name, byte role) {
		List<Players> players = session.createCriteria(Players.class).add(eq("name", name)).add(eq("role", role)).list();
		return players.isEmpty() ? null : data.create(players.get(0));
	}

	public void persist(PositionBacklog backlog) {
		persistObject(backlog);
	}
}
