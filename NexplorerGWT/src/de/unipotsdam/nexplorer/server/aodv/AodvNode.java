package de.unipotsdam.nexplorer.server.aodv;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.gwt.dev.util.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.unipotsdam.nexplorer.server.data.NeighbourAction;
import de.unipotsdam.nexplorer.server.data.NeighbourRoute;
import de.unipotsdam.nexplorer.server.di.InjectLogger;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Neighbour;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.ProcessableDataPacket;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRouteRequestBufferEntries;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingMessages;
import de.unipotsdam.nexplorer.shared.Aodv;
import de.unipotsdam.nexplorer.shared.DataPacket;

public class AodvNode implements NeighbourAction {

	@InjectLogger
	private Logger logger;
	final Player theNode;
	final DatabaseImpl dbAccess;
	private final RoutingTable table;
	private final AodvFactory factory;
	private RREQFactory rreq;

	@Inject
	public AodvNode(@Assisted Player theNode, DatabaseImpl dbAccess, AodvFactory factory, RREQFactory rreq) {
		this.theNode = theNode;
		this.dbAccess = dbAccess;
		this.table = new RoutingTable(this, dbAccess);
		this.factory = factory;
		this.rreq = rreq;
	}

	public Long getId() {
		return theNode.getId();
	}

	public boolean hasBattery() {
		return theNode.hasBattery();
	}

	void aodvProcessDataPackets(long currentDataProcessingRound, List<Neighbour> allKnownNeighbours, long currentRoutingRound) {
		logger.trace("***Datenpakete bei Knoten " + theNode.getId() + "***");

		// ältestes Paket zuerst bearbeiten
		DataPacketQueue packets = new DataPacketQueue(getAllDataPacketsSortedByDate(currentDataProcessingRound));

		// Nur das erste Paket bearbeiten und alle anderen in Wartestellung setzen
		packets.poll().process(currentDataProcessingRound, currentRoutingRound, this, allKnownNeighbours);
		packets.placeContentOnHoldUntil(currentDataProcessingRound + 1);

		for (ProcessableDataPacket packet : packets) {
			logger.trace("Datenpaket mit sourceId " + packet.getSource().getId() + " und destinationId " + packet.getDestination().getId() + " in Wartestellung setzen.");
		}
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

	Collection<Object> aodvProcessRoutingMessages(AodvRoutingAlgorithm aodvRoutingAlgorithm, List<AodvRoutingMessage> nodeRERRs, List<AodvRoutingMessage> routeRequestsByNodeAndRound, List<AodvRouteRequestBufferEntries> allRouteRequestBufferEntries) {
		Collection<Object> persistables = processRREQs(aodvRoutingAlgorithm, routeRequestsByNodeAndRound, allRouteRequestBufferEntries);
		processRERRs(aodvRoutingAlgorithm, nodeRERRs);

		return persistables;
	}

	private void processRERRs(AodvRoutingAlgorithm aodvRoutingAlgorithm, List<AodvRoutingMessage> nodeRERRs) {
		logger.trace("***RERRs bei Knoten " + theNode.getId() + "***");
		for (AodvRoutingMessage theRERR : nodeRERRs) {
			// Prüfen ob Einträge in meiner Routingtabelle betroffen sind
			Long destinationId = theRERR.inner().getDestinationId();
			if (table.hasRouteTo(destinationId)) {
				AodvNode nextHop = table.getNextHop(destinationId);
				logger.trace("RERR mit sourceId " + theRERR.inner().getSourceId() + " und destinationId " + theRERR.inner().getDestinationId() + " betrifft Routingtabelleneintrag mit nextHopId " + nextHop.getId() + ".");

				// RRER an Nachbarn weitersenden
				for (Player neigh : theNode.getNeighbours()) {
					AodvNode next = factory.create(neigh);
					Link link = factory.create(this, next);
					link.transmit(theRERR.inner());
				}

				// Routingtabelleneintrag löschen
				logger.trace("Lösche Routingtabelleneintrag mit nextHopId " + nextHop.getId() + " und destinationId " + theRERR.inner().getDestinationId() + ".");
				table.deleteRouteTo(destinationId);

				logger.trace("RERR mit sourceId " + theRERR.inner().getSourceId() + " und destinationId " + theRERR.inner().getDestinationId() + " löschen, weil er fertig bearbeitet ist.");
			} else {
				logger.trace("RERR mit sourceId " + theRERR.inner().getSourceId() + " und destinationId " + theRERR.inner().getDestinationId() + " löschen, weil uninteressant.");
			}

			// RERR löschen
			theRERR.delete();
		}
	}

	private Collection<Object> processRREQs(AodvRoutingAlgorithm aodv, List<AodvRoutingMessage> routeRequestsByNodeAndRound, List<AodvRouteRequestBufferEntries> allRouteRequestBufferEntries) {
		logger.trace("***RREQs bei Knoten " + theNode.getId() + "***");

		Collection<Object> persistables = new ArrayList<Object>(100);
		for (AodvRoutingMessage theRREQ : routeRequestsByNodeAndRound) {
			Collection<Object> result = processRREQ(aodv, theRREQ, allRouteRequestBufferEntries);
			persistables.addAll(result);
		}

		return persistables;
	}

	private Collection<Object> processRREQ(AodvRoutingAlgorithm aodv, AodvRoutingMessage theRREQ, List<AodvRouteRequestBufferEntries> allRouteRequestBufferEntries) {
		Collection<Object> persistables = new ArrayList<Object>();
		long destination = theRREQ.inner().getDestinationId();
		if (!theRREQ.isExpired() && !hasRREQInBuffer(theRREQ, allRouteRequestBufferEntries)) {
			if (this.isDestinationOf(theRREQ) || table.hasRouteTo(destination)) {
				Collection<Object> result = createRouteForRREQ(theRREQ, table.getHopCountTo(destination));
				persistables.addAll(result);
			} else {
				// RREQ an Nachbarn weitersenden
				for (Player neigh : theNode.getNeighbours()) {
					AodvNode next = factory.create(neigh);
					Link link = factory.create(this, next);
					Collection<Object> result = link.transmit(theRREQ);
					persistables.addAll(result);
				}
			}
		}

		// RREQ entfernen
		theRREQ.delete();

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

	Collection<Object> addRouteRequestToBuffer(AodvRoutingMessage theRequest) {
		logger.trace("RREQ mit sourceId " + theRequest.inner().getSourceId() + " und sequenceNumber " + theRequest.inner().getSequenceNumber() + " zum Puffer hinzufügen.\n");

		AodvRouteRequestBufferEntries newBufferEntry = new AodvRouteRequestBufferEntries();
		newBufferEntry.setNodeId(getId());
		newBufferEntry.setSourceId(theRequest.inner().getSourceId());
		newBufferEntry.setSequenceNumber(theRequest.inner().getSequenceNumber());
		return Arrays.asList((Object) newBufferEntry);
	}

	public void sendRERRToNeighbours(Player errorPlayer, List<Neighbour> allKnownNeighbours, long currentRoutingRound) {
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
			link.transmit(newRERR);

			theNode.save();
		}
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

	Collection<Object> createRouteForRREQ(AodvRoutingMessage theRequest, Long hopCountModifier) {
		// RREQ in Buffer eintragen
		Collection<Object> persistables = addRouteRequestToBuffer(theRequest);

		logger.trace("Route für RREQ mit sourceId " + theRequest.inner().getSourceId() + " und sequenceNumber " + theRequest.inner().getSequenceNumber() + " erstellen.\n");

		long hopCount = 1 + hopCountModifier;

		// Route rückwärts gehen
		List<Long> backwardsRoute = new PassedNodes(theRequest.inner().getPassedNodes());
		Collections.reverse(backwardsRoute);

		Long lastNodeId = getId();
		for (Long theNodeId : backwardsRoute) {
			long dest = theRequest.inner().getDestinationId();
			long sequenceNumber = theRequest.inner().getSequenceNumber();

			RoutingTable.addRoute(theNodeId, lastNodeId, dest, hopCount, sequenceNumber, dbAccess);

			lastNodeId = theNodeId;
			hopCount++;
		}

		return persistables;
	}

	Collection<Object> sendRREQToNeighbours(Player dest, Setting gameSettings) {
		Collection<Object> persistables = new ArrayList<Object>();

		logger.trace("RREQ an alle Nachbarn für Route zum Knoten mit ID " + dest.getId() + " senden.\n");

		for (Player theNeighbour : theNode.getNeighbours()) {
			AodvRoutingMessages newRREQ = new AodvRoutingMessages(theNeighbour.getId(), Aodv.ROUTING_MESSAGE_TYPE_RREQ, getId(), dest.getId(), 9l, theNode.incSequenceNumber(), 0l, null, gameSettings.getCurrentRoutingRound() + 1);
			theNode.save();

			AodvNode next = factory.create(theNeighbour);
			Link link = factory.create(this, next);
			Collection<Object> result = link.transmit(factory.create(newRREQ));
			persistables.addAll(result);
		}

		return persistables;
	}

	public void aodvNeighbourFound(Player destination) {
		table.add(new NeighbourRoute(destination));
		theNode.save();
		destination.save();
	}

	public void aodvNeighbourLost(Player exNeighbour, List<Neighbour> allKnownNeighbours, long currentRoutingRound) {
		sendRERRToNeighbours(exNeighbour, allKnownNeighbours, currentRoutingRound);
		table.deleteRouteTo(exNeighbour.getId());
	}

	Collection<Object> enqueMessage(DataPacket message) {
		long destination = message.getMessageDescription().getDestinationNodeId();
		Collection<Object> persistables = new ArrayList<Object>();

		if (!theNode.hasBattery()) {
			return Collections.emptyList();
		}

		if (table.hasRouteTo(destination)) {
			send(message).toDestination();
		} else {
			pause(message);
			Collection<Object> result = sendRREQFor(destination).toNeighbours();
			persistables.addAll(result);
		}

		persistables.add(message);
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

	public void updateNeighbourhood(List<Neighbour> allKnownNeighbours, long currentRoutingRound) {
		theNode.updateNeighbourhood(this, allKnownNeighbours, currentRoutingRound);
	}

	public void pingNeighbourhood() {
		theNode.pingNeighbourhood();
	}
}
