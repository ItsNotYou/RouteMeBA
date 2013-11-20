package de.unipotsdam.nexplorer.server.aodv;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.unipotsdam.nexplorer.server.PojoAction;
import de.unipotsdam.nexplorer.server.di.InjectLogger;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingMessages;
import de.unipotsdam.nexplorer.shared.Aodv;

public class RREQDestination {

	@InjectLogger
	private Logger logger;
	private long destId;
	private AodvNode theNode;
	private DatabaseImpl dbAccess;
	private AodvFactory factory;

	@Inject
	public RREQDestination(@Assisted AodvNode node, @Assisted long destinationId, DatabaseImpl dbAccess, AodvFactory factory) {
		this.destId = destinationId;
		this.theNode = node;
		this.dbAccess = dbAccess;
		this.factory = factory;
	}

	public Map<Object, PojoAction> toNeighbours() {
		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();
		Setting gameSettings = dbAccess.getSettings();

		logger.info("RREQ an alle Nachbarn für Route zum Knoten mit ID {} senden.", destId);

		for (Player theNeighbour : theNode.theNode.getNeighbours()) {
			AodvRoutingMessages newRREQ = new AodvRoutingMessages(theNeighbour.getId(), Aodv.ROUTING_MESSAGE_TYPE_RREQ, theNode.getId(), destId, 9l, theNode.player().incSequenceNumber(), 0l, null, gameSettings.getCurrentRoutingRound() + 1);
			theNode.theNode.save();

			AodvNode next = factory.create(theNeighbour);
			Link link = factory.create(theNode, next);
			Map<Object, PojoAction> result = link.transmit(factory.create(newRREQ), gameSettings);
			persistables.putAll(result);
		}

		return persistables;
	}
}
