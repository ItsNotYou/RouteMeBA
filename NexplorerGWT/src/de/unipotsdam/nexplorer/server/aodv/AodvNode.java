package de.unipotsdam.nexplorer.server.aodv;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.gwt.dev.util.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.unipotsdam.nexplorer.server.PojoAction;
import de.unipotsdam.nexplorer.server.data.NeighbourAction;
import de.unipotsdam.nexplorer.server.data.NeighbourRoute;
import de.unipotsdam.nexplorer.server.di.InjectLogger;
import de.unipotsdam.nexplorer.server.persistence.Neighbour;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.ProcessableDataPacket;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRouteRequestBufferEntries;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingMessages;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingTableEntries;
import de.unipotsdam.nexplorer.shared.Aodv;
import de.unipotsdam.nexplorer.shared.DataPacket;

public class AodvNode implements NeighbourAction {

	@InjectLogger
	private Logger logger;
	final Player theNode;
	private final AodvFactory factory;
	private RREQFactory rreq;

	@Inject
	public AodvNode(@Assisted Player theNode, AodvFactory factory, RREQFactory rreq) {
		this.theNode = theNode;
		this.factory = factory;
		this.rreq = rreq;
	}

	private RoutingTable table(List<AodvRoutingTableEntries> routingTable) {
		return new RoutingTable(this, routingTable);
	}

	public Long getId() {
		return theNode.getId();
	}

	public boolean hasBattery() {
		return theNode.hasBattery();
	}

	Map<Object, PojoAction> aodvProcessDataPackets(long currentDataProcessingRound, List<Neighbour> allKnownNeighbours, long currentRoutingRound, List<AodvRoutingTableEntries> routingTable, Setting gameSettings) {
		logger.trace("***Datenpakete bei Knoten " + theNode.getId() + "***");
		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();

		// ältestes Paket zuerst bearbeiten
		DataPacketQueue packets = new DataPacketQueue(getAllDataPacketsSortedByDate(currentDataProcessingRound));

		// Nur das erste Paket bearbeiten und alle anderen in Wartestellung setzen
		HashMap<Object, PojoAction> result = packets.poll().process(currentDataProcessingRound, currentRoutingRound, this, allKnownNeighbours, routingTable, gameSettings);
		persistables.putAll(result);
		packets.placeContentOnHoldUntil(currentDataProcessingRound + 1);

		for (ProcessableDataPacket packet : packets) {
			logger.trace("Datenpaket mit sourceId " + packet.getSource().getId() + " und destinationId " + packet.getDestination().getId() + " in Wartestellung setzen.");
		}

		return persistables;
	}

	/**
	 * Criteria for the returned data packets:<br/>
	 * - the packet status is NOT arrived<br/>
	 * - the packet status is NOT cancelled<br/>
	 * - the packet processing round is equal to the given<br/>
	 * - the packet is currently at this node
	 * 
	 * @return The resulting list sorted by ascending id
	 */
	private List<? extends ProcessableDataPacket> getAllDataPacketsSortedByDate(final long currentDataRount) {
		Collection<AodvDataPackets> packetses = this.theNode.getCurrentDataPackets();
		packetses = filter(packetses, new Predicate<AodvDataPackets>() {

			@Override
			public boolean apply(AodvDataPackets arg0) {
				if (arg0.getStatus() != null) {
					if (arg0.getStatus() == Aodv.DATA_PACKET_STATUS_ARRIVED || arg0.getStatus() == Aodv.DATA_PACKET_STATUS_CANCELLED) {
						return false;
					}
				}
				return true;
			}
		});
		packetses = filter(packetses, new Predicate<AodvDataPackets>() {

			@Override
			public boolean apply(AodvDataPackets arg0) {
				if (arg0.getProcessingRound() != null && arg0.getProcessingRound() == currentDataRount) {
					return true;
				}
				return false;
			}
		});
		Collection<AodvDataPacket> packets = transform(packetses, new Function<AodvDataPackets, AodvDataPacket>() {

			@Override
			public AodvDataPacket apply(AodvDataPackets arg0) {
				return factory.create(arg0);
			}
		});

		return Lists.create(packets);
	}

	Map<Object, PojoAction> aodvProcessRoutingMessages(AodvRoutingAlgorithm aodvRoutingAlgorithm, List<AodvRoutingMessage> nodeRERRs, List<AodvRoutingMessage> routeRequestsByNodeAndRound, List<AodvRouteRequestBufferEntries> allRouteRequestBufferEntries, List<AodvRoutingTableEntries> allRoutingTableEntries, Setting gameSettings) {
		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();

		Map<Object, PojoAction> result = processRREQs(aodvRoutingAlgorithm, routeRequestsByNodeAndRound, allRouteRequestBufferEntries, allRoutingTableEntries, gameSettings);
		persistables.putAll(result);

		result = processRERRs(aodvRoutingAlgorithm, nodeRERRs, allRoutingTableEntries, gameSettings);
		persistables.putAll(result);

		return persistables;
	}

	private Map<Object, PojoAction> processRERRs(AodvRoutingAlgorithm aodvRoutingAlgorithm, List<AodvRoutingMessage> nodeRERRs, List<AodvRoutingTableEntries> routingTable, Setting gameSettings) {
		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();

		logger.trace("***RERRs bei Knoten " + theNode.getId() + "***");
		for (AodvRoutingMessage theRERR : nodeRERRs) {
			// Prüfen ob Einträge in meiner Routingtabelle betroffen sind
			Long destinationId = theRERR.inner().getDestinationId();
			if (table(routingTable).hasRouteTo(destinationId)) {
				long nextHopId = table(routingTable).getNextHop(destinationId);
				logger.trace("RERR mit sourceId " + theRERR.inner().getSourceId() + " und destinationId " + theRERR.inner().getDestinationId() + " betrifft Routingtabelleneintrag mit nextHopId " + nextHopId + ".");

				// RRER an Nachbarn weitersenden
				for (Player neigh : theNode.getNeighbours()) {
					AodvNode next = factory.create(neigh);
					Link link = factory.create(this, next);
					Map<Object, PojoAction> result = link.transmit(theRERR.inner(), gameSettings);
					persistables.putAll(result);
				}

				// Routingtabelleneintrag löschen
				logger.trace("Lösche Routingtabelleneintrag mit nextHopId " + nextHopId + " und destinationId " + theRERR.inner().getDestinationId() + ".");
				Map<Object, PojoAction> result = table(routingTable).deleteRouteTo(destinationId);
				persistables.putAll(result);

				logger.trace("RERR mit sourceId " + theRERR.inner().getSourceId() + " und destinationId " + theRERR.inner().getDestinationId() + " löschen, weil er fertig bearbeitet ist.");
			} else {
				logger.trace("RERR mit sourceId " + theRERR.inner().getSourceId() + " und destinationId " + theRERR.inner().getDestinationId() + " löschen, weil uninteressant.");
			}

			// RERR löschen
			Map<Object, PojoAction> result = theRERR.delete();
			persistables.putAll(result);
		}

		return persistables;
	}

	private Map<Object, PojoAction> processRREQs(AodvRoutingAlgorithm aodv, List<AodvRoutingMessage> routeRequestsByNodeAndRound, List<AodvRouteRequestBufferEntries> allRouteRequestBufferEntries, List<AodvRoutingTableEntries> allRoutingTableEntries, Setting gameSettings) {
		logger.trace("***RREQs bei Knoten " + theNode.getId() + "***");

		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>(100);
		for (AodvRoutingMessage theRREQ : routeRequestsByNodeAndRound) {
			Map<Object, PojoAction> result = processRREQ(aodv, theRREQ, allRouteRequestBufferEntries, allRoutingTableEntries, gameSettings);
			persistables.putAll(result);
		}

		return persistables;
	}

	private Map<Object, PojoAction> processRREQ(AodvRoutingAlgorithm aodv, AodvRoutingMessage theRREQ, List<AodvRouteRequestBufferEntries> allRouteRequestBufferEntries, List<AodvRoutingTableEntries> allRoutingTableEntries, Setting gameSettings) {
		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();
		long destination = theRREQ.inner().getDestinationId();
		if (!theRREQ.isExpired() && !hasRREQInBuffer(theRREQ, allRouteRequestBufferEntries)) {
			if (this.isDestinationOf(theRREQ) || table(allRoutingTableEntries).hasRouteTo(destination)) {
				Map<Object, PojoAction> result = createRouteForRREQ(theRREQ, table(allRoutingTableEntries).getHopCountTo(destination), allRoutingTableEntries);
				persistables.putAll(result);
			} else {
				// RREQ an Nachbarn weitersenden
				for (Player neigh : theNode.getNeighbours()) {
					AodvNode next = factory.create(neigh);
					Link link = factory.create(this, next);
					Map<Object, PojoAction> result = link.transmit(theRREQ, gameSettings);
					persistables.putAll(result);
				}
			}
		}

		// RREQ entfernen
		Map<Object, PojoAction> result = theRREQ.delete();
		persistables.putAll(result);

		return persistables;
	}

	private boolean isDestinationOf(AodvRoutingMessage theRREQ) {
		return theRREQ.inner().getDestinationId() == theNode.getId();
	}

	public Player player() {
		return theNode;
	}

	private boolean hasRREQInBuffer(final AodvRoutingMessage theRREQ, List<AodvRouteRequestBufferEntries> allRouteRequestBufferEntries) {
		final Player theNode = this.theNode;
		Collection<AodvRouteRequestBufferEntries> entries = Collections2.filter(allRouteRequestBufferEntries, new Predicate<AodvRouteRequestBufferEntries>() {

			@Override
			public boolean apply(AodvRouteRequestBufferEntries entry) {
				if (entry.getNodeId() != theNode.getId()) {
					return false;
				}
				if (entry.getSourceId() != theRREQ.inner().getSourceId()) {
					return false;
				}
				if (entry.getSequenceNumber() != theRREQ.inner().getSequenceNumber()) {
					return false;
				}

				return true;
			}
		});

		return entries.isEmpty() ? false : true;
	}

	Map<Object, PojoAction> addRouteRequestToBuffer(AodvRoutingMessage theRequest) {
		HashMap<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();
		logger.trace("RREQ mit sourceId " + theRequest.inner().getSourceId() + " und sequenceNumber " + theRequest.inner().getSequenceNumber() + " zum Puffer hinzufügen.\n");

		AodvRouteRequestBufferEntries newBufferEntry = new AodvRouteRequestBufferEntries();
		newBufferEntry.setNodeId(getId());
		newBufferEntry.setSourceId(theRequest.inner().getSourceId());
		newBufferEntry.setSequenceNumber(theRequest.inner().getSequenceNumber());
		persistables.put(newBufferEntry, PojoAction.SAVE);

		return persistables;
	}

	public Map<Object, PojoAction> sendRERRToNeighbours(Player errorPlayer, List<Neighbour> allKnownNeighbours, long currentRoutingRound, Setting gameSettings) {
		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();

		List<Neighbour> neighbours = getAllNeighboursExcept(errorPlayer, allKnownNeighbours);
		for (Neighbour theNeighbour : neighbours) {
			AodvRoutingMessages newRERR = new AodvRoutingMessages();
			newRERR.setType(Aodv.ROUTING_MESSAGE_TYPE_RERR);
			newRERR.setDestinationId(errorPlayer.getId());
			newRERR.setSourceId(getId());
			newRERR.setCurrentNodeId(theNeighbour.getNeighbour().getId());
			newRERR.setSequenceNumber(theNode.incSequenceNumber());
			newRERR.setProcessingRound(currentRoutingRound + 1);

			AodvNode next = factory.create(theNeighbour.getNeighbour());
			Link link = factory.create(this, next);
			Map<Object, PojoAction> result = link.transmit(newRERR, gameSettings);
			persistables.putAll(result);

			theNode.save();
		}

		return persistables;
	}

	private List<Neighbour> getAllNeighboursExcept(final Player errorPlayer, List<Neighbour> allKnownNeighbours) {
		Collection<Neighbour> result = Collections2.filter(allKnownNeighbours, new Predicate<Neighbour>() {

			@Override
			public boolean apply(Neighbour arg0) {
				return arg0.getNeighbour().getId() != errorPlayer.getId();
			}
		});
		return new ArrayList<Neighbour>(result);
	}

	Map<Object, PojoAction> createRouteForRREQ(AodvRoutingMessage theRequest, Long hopCountModifier, List<AodvRoutingTableEntries> allRoutingTableEntries) {
		Map<Object, PojoAction> result = new HashMap<Object, PojoAction>();

		// RREQ in Buffer eintragen
		Map<Object, PojoAction> persistables = addRouteRequestToBuffer(theRequest);
		result.putAll(persistables);

		logger.trace("Route für RREQ mit sourceId " + theRequest.inner().getSourceId() + " und sequenceNumber " + theRequest.inner().getSequenceNumber() + " erstellen.\n");

		long hopCount = 1 + hopCountModifier;

		// Route rückwärts gehen
		List<Long> backwardsRoute = new PassedNodes(theRequest.inner().getPassedNodes());
		Collections.reverse(backwardsRoute);

		Long lastNodeId = getId();
		for (Long theNodeId : backwardsRoute) {
			long dest = theRequest.inner().getDestinationId();
			long sequenceNumber = theRequest.inner().getSequenceNumber();

			persistables = RoutingTable.addRoute(theNodeId, lastNodeId, dest, hopCount, sequenceNumber, allRoutingTableEntries);
			result.putAll(persistables);

			lastNodeId = theNodeId;
			hopCount++;
		}

		return result;
	}

	Map<Object, PojoAction> sendRREQToNeighbours(Player dest, Setting gameSettings) {
		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();

		logger.trace("RREQ an alle Nachbarn für Route zum Knoten mit ID " + dest.getId() + " senden.\n");

		for (Player theNeighbour : theNode.getNeighbours()) {
			AodvRoutingMessages newRREQ = new AodvRoutingMessages(theNeighbour.getId(), Aodv.ROUTING_MESSAGE_TYPE_RREQ, getId(), dest.getId(), 9l, theNode.incSequenceNumber(), 0l, null, gameSettings.getCurrentRoutingRound() + 1);
			theNode.save();

			AodvNode next = factory.create(theNeighbour);
			Link link = factory.create(this, next);
			Map<Object, PojoAction> result = link.transmit(factory.create(newRREQ), gameSettings);
			persistables.putAll(result);
		}

		return persistables;
	}

	@Override
	public Map<Object, PojoAction> aodvNeighbourFound(Player destination, List<AodvRoutingTableEntries> routingTable) {
		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();

		Map<Object, PojoAction> result = table(routingTable).add(new NeighbourRoute(destination));
		persistables.putAll(result);
		theNode.save();
		destination.save();

		return persistables;
	}

	@Override
	public Map<Object, PojoAction> aodvNeighbourLost(Player exNeighbour, List<Neighbour> allKnownNeighbours, long currentRoutingRound, List<AodvRoutingTableEntries> routingTable, Setting gameSettings) {
		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();
		Map<Object, PojoAction> result = sendRERRToNeighbours(exNeighbour, allKnownNeighbours, currentRoutingRound, gameSettings);
		persistables.putAll(result);
		result = table(routingTable).deleteRouteTo(exNeighbour.getId());
		persistables.putAll(result);
		return persistables;
	}

	Map<Object, PojoAction> enqueMessage(DataPacket message, List<AodvRoutingTableEntries> routingTable) {
		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();
		long destination = message.getMessageDescription().getDestinationNodeId();

		if (!theNode.hasBattery()) {
			return new HashMap<Object, PojoAction>();
		}

		if (table(routingTable).hasRouteTo(destination)) {
			send(message).toDestination();
		} else {
			pause(message);
			Map<Object, PojoAction> result = sendRREQFor(destination).toNeighbours();
			persistables.putAll(result);
		}

		persistables.put(message, PojoAction.SAVE);
		return persistables;
	}

	private RREQDestination sendRREQFor(long destination) {
		return rreq.from(this).to(destination);
	}

	Destination send(DataPacket message) {
		return new Destination(message);
	}

	void pause(DataPacket newMessage) {
		// Nachricht in Warteschleife setzen
		newMessage.setStatus(Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE);
	}

	public Map<Object, PojoAction> updateNeighbourhood(List<Neighbour> allKnownNeighbours, long currentRoutingRound, List<AodvRoutingTableEntries> routingTable, Setting gameSettings) {
		return theNode.updateNeighbourhood(this, allKnownNeighbours, currentRoutingRound, routingTable, gameSettings);
	}

	public void pingNeighbourhood() {
		theNode.pingNeighbourhood();
	}
}
