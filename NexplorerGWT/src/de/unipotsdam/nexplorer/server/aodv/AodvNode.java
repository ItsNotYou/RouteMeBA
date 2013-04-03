package de.unipotsdam.nexplorer.server.aodv;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;

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

	void aodvProcessDataPackets(long currentDataProcessingRound) {
		logger.trace("***Datenpakete bei Knoten " + theNode.getId() + "***");

		// ältestes Paket zuerst bearbeiten
		DataPacketQueue packets = new DataPacketQueue(dbAccess.getAllDataPacketsSortedByDate(theNode));

		// Nur das erste Paket bearbeiten und alle anderen in Wartestellung setzen
		packets.poll().process(currentDataProcessingRound, this);
		packets.placeContentOnHoldUntil(currentDataProcessingRound + 1);

		for (ProcessableDataPacket packet : packets) {
			logger.trace("Datenpaket mit sourceId " + packet.getSource().getId() + " und destinationId " + packet.getDestination().getId() + " in Wartestellung setzen.");
		}
	}

	void aodvProcessRoutingMessages(AodvRoutingAlgorithm aodvRoutingAlgorithm) {
		processRREQs(aodvRoutingAlgorithm);
		processRERRs(aodvRoutingAlgorithm);
	}

	private void processRERRs(AodvRoutingAlgorithm aodvRoutingAlgorithm) {
		logger.trace("***RERRs bei Knoten " + theNode.getId() + "***");
		List<AodvRoutingMessage> nodeRERRs = dbAccess.getRoutingErrors(theNode);
		for (AodvRoutingMessage theRERR : nodeRERRs) {
			// Prüfen ob Einträge in meiner Routingtabelle betroffen sind
			Player destination = dbAccess.getPlayerById(theRERR.inner().getDestinationId());
			if (table.hasRouteTo(destination.getId())) {
				AodvNode nextHop = table.getNextHop(destination.getId());
				logger.trace("RERR mit sourceId " + theRERR.inner().getSourceId() + " und destinationId " + theRERR.inner().getDestinationId() + " betrifft Routingtabelleneintrag mit nextHopId " + nextHop.getId() + ".");

				// RRER an Nachbarn weitersenden
				for (Player neigh : theNode.getNeighbours()) {
					AodvNode next = factory.create(neigh);
					Link link = factory.create(this, next);
					link.transmit(theRERR.inner());
				}

				// Routingtabelleneintrag löschen
				logger.trace("Lösche Routingtabelleneintrag mit nextHopId " + nextHop.getId() + " und destinationId " + theRERR.inner().getDestinationId() + ".");
				table.deleteRouteTo(destination.getId());

				logger.trace("RERR mit sourceId " + theRERR.inner().getSourceId() + " und destinationId " + theRERR.inner().getDestinationId() + " löschen, weil er fertig bearbeitet ist.");
			} else {
				logger.trace("RERR mit sourceId " + theRERR.inner().getSourceId() + " und destinationId " + theRERR.inner().getDestinationId() + " löschen, weil uninteressant.");
			}

			// RERR löschen
			theRERR.delete();
		}
	}

	private void processRREQs(AodvRoutingAlgorithm aodv) {
		logger.trace("***RREQs bei Knoten " + theNode.getId() + "***");
		for (AodvRoutingMessage theRREQ : dbAccess.getRouteRequestsByNodeAndRound(theNode)) {
			processRREQ(aodv, theRREQ);
		}
	}

	private void processRREQ(AodvRoutingAlgorithm aodv, AodvRoutingMessage theRREQ) {
		long destination = theRREQ.inner().getDestinationId();
		Player dest = dbAccess.getPlayerById(destination);
		if (!theRREQ.isExpired() && !hasRREQInBuffer(theRREQ)) {
			if (this.isDestinationOf(theRREQ) || table.hasRouteTo(factory.create(dest))) {
				createRouteForRREQ(theRREQ, table.getHopCountTo(factory.create(dest)));
			} else {
				// RREQ an Nachbarn weitersenden
				for (Player neigh : theNode.getNeighbours()) {
					AodvNode next = factory.create(neigh);
					Link link = factory.create(this, next);
					link.transmit(theRREQ);
				}
			}
		}

		// RREQ entfernen
		theRREQ.delete();
	}

	private boolean isDestinationOf(AodvRoutingMessage theRREQ) {
		return theRREQ.inner().getDestinationId() == theNode.getId();
	}

	public Player player() {
		return theNode;
	}

	private boolean hasRREQInBuffer(AodvRoutingMessage theRREQ) {
		AodvRouteRequestBufferEntry RREQBufferEntry = dbAccess.getAODVRouteRequestBufferEntry(theNode, theRREQ);
		return RREQBufferEntry != null;
	}

	void addRouteRequestToBuffer(AodvRoutingMessage theRequest) {
		logger.trace("RREQ mit sourceId " + theRequest.inner().getSourceId() + " und sequenceNumber " + theRequest.inner().getSequenceNumber() + " zum Puffer hinzufügen.\n");

		AodvRouteRequestBufferEntries newBufferEntry = new AodvRouteRequestBufferEntries();
		newBufferEntry.setNodeId(getId());
		newBufferEntry.setSourceId(theRequest.inner().getSourceId());
		newBufferEntry.setSequenceNumber(theRequest.inner().getSequenceNumber());
		dbAccess.persist(newBufferEntry);
	}

	public void sendRERRToNeighbours(Player errorPlayer) {
		Setting gameSettings = dbAccess.getSettings();

		List<Neighbour> neighbours = dbAccess.getAllNeighboursExcept(errorPlayer, theNode);
		for (Neighbour theNeighbour : neighbours) {
			AodvRoutingMessages newRERR = new AodvRoutingMessages();
			newRERR.setType(Aodv.ROUTING_MESSAGE_TYPE_RERR);
			newRERR.setDestinationId(errorPlayer.getId());
			newRERR.setSourceId(getId());
			newRERR.setCurrentNodeId(theNeighbour.getNeighbour().getId());
			newRERR.setSequenceNumber(theNode.incSequenceNumber());
			newRERR.setProcessingRound(gameSettings.getCurrentRoutingRound() + 1);

			AodvNode next = factory.create(theNeighbour.getNeighbour());
			Link link = factory.create(this, next);
			link.transmit(newRERR);

			theNode.save();
		}
	}

	void createRouteForRREQ(AodvRoutingMessage theRequest, Long hopCountModifier) {
		// RREQ in Buffer eintragen
		addRouteRequestToBuffer(theRequest);

		logger.trace("Route für RREQ mit sourceId " + theRequest.inner().getSourceId() + " und sequenceNumber " + theRequest.inner().getSequenceNumber() + " erstellen.\n");

		long hopCount = 1 + hopCountModifier;

		// Route rückwärts gehen
		List<Long> backwardsRoute = new PassedNodes(theRequest.inner().getPassedNodes());
		Collections.reverse(backwardsRoute);

		Long lastNodeId = getId();
		for (Long theNodeId : backwardsRoute) {
			long dest = theRequest.inner().getDestinationId();
			long sequenceNumber = theRequest.inner().getSequenceNumber();

			Player player = dbAccess.getPlayerById(theNodeId);
			AodvNode node = factory.create(player);
			RoutingTable table = new RoutingTable(node, dbAccess);
			table.addRoute(lastNodeId, dest, hopCount, sequenceNumber);

			lastNodeId = theNodeId;
			hopCount++;
		}
	}

	void sendRREQToNeighbours(Player dest) {
		Setting gameSettings = dbAccess.getSettings();

		logger.trace("RREQ an alle Nachbarn für Route zum Knoten mit ID " + dest.getId() + " senden.\n");

		for (Player theNeighbour : theNode.getNeighbours()) {
			AodvRoutingMessages newRREQ = new AodvRoutingMessages(theNeighbour.getId(), Aodv.ROUTING_MESSAGE_TYPE_RREQ, getId(), dest.getId(), 9l, theNode.incSequenceNumber(), 0l, null, gameSettings.getCurrentRoutingRound() + 1);
			theNode.save();

			AodvNode next = factory.create(theNeighbour);
			Link link = factory.create(this, next);
			link.transmit(factory.create(newRREQ));
		}
	}

	public void aodvNeighbourFound(Player destination) {
		table.add(new NeighbourRoute(destination));
		theNode.save();
		destination.save();
	}

	public void aodvNeighbourLost(Player exNeighbour) {
		sendRERRToNeighbours(exNeighbour);
		table.deleteRouteTo(exNeighbour.getId());
	}

	void enqueMessage(DataPacket message) {
		long destination = message.getMessageDescription().getDestinationNodeId();

		if (!theNode.hasBattery()) {
			return;
		}

		if (table.hasRouteTo(destination)) {
			send(message).toDestination();
		} else {
			pause(message);
			sendRREQFor(destination).toNeighbours();
		}
		dbAccess.persist(message);
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

	public void updateNeighbourhood() {
		theNode.updateNeighbourhood(this);
	}

	public void pingNeighbourhood() {
		theNode.pingNeighbourhood();
	}
}
