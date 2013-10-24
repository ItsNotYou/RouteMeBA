package de.unipotsdam.nexplorer.server.aodv;

import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.unipotsdam.nexplorer.server.di.InjectLogger;
import de.unipotsdam.nexplorer.server.persistence.DataFactory;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.ProcessableDataPacket;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.shared.Aodv;

public class AodvDataPacket implements ProcessableDataPacket {

	@InjectLogger
	private Logger logger;
	private final AodvDataPackets inner;
	private final DatabaseImpl dbAccess;
	private final AodvFactory factory;
	private DataFactory data;

	@Inject
	public AodvDataPacket(@Assisted AodvDataPackets inner, DatabaseImpl dbAccess, AodvFactory factory, DataFactory data) {
		this.inner = inner;
		this.dbAccess = dbAccess;
		this.factory = factory;
		this.data = data;
	}

	@Override
	public AodvDataPackets inner() {
		return this.inner;
	}

	@Override
	public AodvNode getDestination() {
		Player destination = data.create(inner.getPlayersByDestinationId());
		return factory.create(destination);
	}

	@Override
	public AodvNode getSource() {
		Player source = data.create(inner.getPlayersBySourceId());
		return factory.create(source);
	}

	public void delete() {
		dbAccess.delete(inner);
	}

	@Override
	public void save() {
		dbAccess.persist(inner);
	}

	@Override
	public void setOnHoldUntil(long dataProcessingRound) {
		inner.setStatus(Aodv.DATA_PACKET_STATUS_NODE_BUSY);
		inner.setProcessingRound(dataProcessingRound);
	}

	@Override
	public void process(long currentDataProcessingRound, AodvNode aodvNode) {
		Byte status = inner.getStatus();
		switch (status) {
		case Aodv.DATA_PACKET_STATUS_UNDERWAY:
		case Aodv.DATA_PACKET_STATUS_NODE_BUSY:
			// Pakete ist unterwegs oder wartet darauf versendet zu werden
			forwardPacket(aodvNode);
			break;
		case Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE:
		case Aodv.DATA_PACKET_STATUS_ERROR:
			// Paket ist in Wartestellung (Route war anf�nglich unbekannt)
			checkAndForward(currentDataProcessingRound, aodvNode);
			break;
		}
	}

	void checkAndForward(long currentDataProcessingRound, AodvNode aodvNode) {
		// prüfen ob mittlerweile Route zum Ziel bekannt
		AodvNode dest = factory.create(data.create(inner.getPlayersByDestinationId()));
		RoutingTable table = new RoutingTable(aodvNode, dbAccess);
		if (table.hasRouteTo(dest)) {
			// Packet weitersenden
			Link conn = factory.create(aodvNode, table.getNextHop(dest));
			conn.transmit(this);

			logger.trace("Datenpaket mit sourceId " + inner.getPlayersBySourceId().getId() + " und destinationId " + inner.getPlayersByDestinationId().getId() + " l�schen, weil fertig bearbeitet.");

			delete();
		} else {
			int RREQCount = dbAccess.getRouteRequestCount(inner);
			if (RREQCount == 0) {
				inner.setStatus(Aodv.DATA_PACKET_STATUS_ERROR);
			} else {
				inner.setStatus(Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE);
			}
			inner.setProcessingRound(currentDataProcessingRound + 1);
			save();
		}
	}

	void forwardPacket(AodvNode aodvNode) {
		// prüfen ob Route zum Ziel bekannt
		Player destination = data.create(inner.getPlayersByDestinationId());
		AodvNode dest = factory.create(destination);
		RoutingTable table = new RoutingTable(aodvNode, dbAccess);
		if (table.hasRouteTo(dest)) {
			// Packet weitersenden
			Link conn = factory.create(aodvNode, table.getNextHop(dest));
			conn.transmit(this);

			// Packet löschen
			logger.trace("Datenpaket mit sourceId " + inner.getPlayersBySourceId().getId() + " und destinationId " + inner.getPlayersByDestinationId().getId() + " löschen, weil fertig bearbeitet.");
			delete();
		} else {
			// RERRs senden (jemand denkt irrtümlich ich würde eine Route kennen)
			aodvNode.sendRERRToNeighbours(destination);

			logger.trace("Datenpacket mit sourceId {} und destinationId {} nicht zustellbar, da keine Route bekannt", inner.getPlayersBySourceId().getId(), inner.getPlayersByDestinationId().getId());
			inner.setStatus(Aodv.DATA_PACKET_STATUS_ERROR);
			save();
		}
	}

	public void setCurrentNode(Player player) {
		player.execute(new AsCurrent(inner));
	}
}
