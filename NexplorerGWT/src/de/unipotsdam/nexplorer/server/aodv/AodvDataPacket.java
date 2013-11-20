package de.unipotsdam.nexplorer.server.aodv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.unipotsdam.nexplorer.server.PojoAction;
import de.unipotsdam.nexplorer.server.data.Maps;
import de.unipotsdam.nexplorer.server.di.InjectLogger;
import de.unipotsdam.nexplorer.server.persistence.DataFactory;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Neighbour;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.ProcessableDataPacket;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingMessages;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingTableEntries;
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

	public Map<Object, PojoAction> delete() {
		return Maps.create(inner, PojoAction.DELETE);
	}

	@Override
	public Map<Object, PojoAction> save() {
		return Maps.create(inner, PojoAction.SAVE);
	}

	@Override
	public void setOnHoldUntil(long dataProcessingRound) {
		inner.setStatus(Aodv.DATA_PACKET_STATUS_NODE_BUSY);
		inner.setProcessingRound(dataProcessingRound);
	}

	@Override
	public Map<Object, PojoAction> process(long currentDataProcessingRound, long currentRoutingRound, AodvNode aodvNode, List<Neighbour> allKnownNeighbours, List<AodvRoutingTableEntries> routingTable, Setting gameSettings, List<AodvRoutingMessages> allRoutingMessages) {
		Map<Object, PojoAction> persistables = Maps.empty();

		Byte status = inner.getStatus();
		switch (status) {
		case Aodv.DATA_PACKET_STATUS_UNDERWAY:
		case Aodv.DATA_PACKET_STATUS_NODE_BUSY:
			// Pakete ist unterwegs oder wartet darauf versendet zu werden
			persistables.putAll(forwardPacket(aodvNode, allKnownNeighbours, currentRoutingRound, routingTable, gameSettings));
			break;
		case Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE:
		case Aodv.DATA_PACKET_STATUS_ERROR:
			// Paket ist in Wartestellung (Route war anf�nglich unbekannt)
			persistables.putAll(checkAndForward(currentDataProcessingRound, aodvNode, routingTable, gameSettings, allRoutingMessages));
			break;
		}

		return persistables;
	}

	Map<Object, PojoAction> checkAndForward(long currentDataProcessingRound, AodvNode aodvNode, List<AodvRoutingTableEntries> routingTable, Setting gameSettings, List<AodvRoutingMessages> allRoutingMessages) {
		Map<Object, PojoAction> persistables = Maps.empty();

		// prüfen ob mittlerweile Route zum Ziel bekannt
		AodvNode dest = factory.create(data.create(inner.getPlayersByDestinationId()));
		RoutingTable table = new RoutingTable(aodvNode, routingTable);
		if (table.hasRouteTo(dest)) {
			// Packet weitersenden
			AodvNode nextHop = factory.create(dbAccess.getPlayerById(table.getNextHop(dest)));
			Link conn = factory.create(aodvNode, nextHop);
			Map<Object, PojoAction> result = conn.transmit(this, gameSettings);
			persistables.putAll(result);

			logger.trace("Datenpaket mit sourceId " + inner.getPlayersBySourceId().getId() + " und destinationId " + inner.getPlayersByDestinationId().getId() + " l�schen, weil fertig bearbeitet.");

			result = delete();
			persistables.putAll(result);
		} else {
			int RREQCount = getRouteRequestCount(inner, allRoutingMessages);
			if (RREQCount == 0) {
				inner.setStatus(Aodv.DATA_PACKET_STATUS_ERROR);
			} else {
				inner.setStatus(Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE);
			}
			inner.setProcessingRound(currentDataProcessingRound + 1);
			persistables.putAll(save());
		}

		return persistables;
	}

	private int getRouteRequestCount(final AodvDataPackets thePacket, List<AodvRoutingMessages> allRoutingMessages) {
		return Collections2.filter(allRoutingMessages, new Predicate<AodvRoutingMessages>() {

			@Override
			public boolean apply(AodvRoutingMessages arg0) {
				if (arg0.getType() != Aodv.ROUTING_MESSAGE_TYPE_RREQ) {
					return false;
				}
				if (arg0.getSourceId() != thePacket.getPlayersBySourceId().getId()) {
					return false;
				}
				if (arg0.getDestinationId() != thePacket.getPlayersByDestinationId().getId()) {
					return false;
				}

				return true;
			}
		}).size();
	}

	Map<Object, PojoAction> forwardPacket(AodvNode aodvNode, List<Neighbour> allKnownNeighbours, long currentRoutingRound, List<AodvRoutingTableEntries> routingTable, Setting gameSettings) {
		Map<Object, PojoAction> persistables = new HashMap<Object, PojoAction>();

		// prüfen ob Route zum Ziel bekannt
		Player destination = data.create(inner.getPlayersByDestinationId());
		AodvNode dest = factory.create(destination);
		RoutingTable table = new RoutingTable(aodvNode, routingTable);
		if (table.hasRouteTo(dest)) {
			// Packet weitersenden
			AodvNode nextHop = factory.create(dbAccess.getPlayerById(table.getNextHop(dest)));
			Link conn = factory.create(aodvNode, nextHop);
			Map<Object, PojoAction> result = conn.transmit(this, gameSettings);
			persistables.putAll(result);

			// Packet löschen
			logger.trace("Datenpaket mit sourceId " + inner.getPlayersBySourceId().getId() + " und destinationId " + inner.getPlayersByDestinationId().getId() + " löschen, weil fertig bearbeitet.");

			result = delete();
			persistables.putAll(result);
		} else {
			// RERRs senden (jemand denkt irrtümlich ich würde eine Route kennen)
			Map<Object, PojoAction> result = aodvNode.sendRERRToNeighbours(destination, allKnownNeighbours, currentRoutingRound, gameSettings);
			persistables.putAll(result);

			logger.trace("Datenpacket mit sourceId {} und destinationId {} nicht zustellbar, da keine Route bekannt", inner.getPlayersBySourceId().getId(), inner.getPlayersByDestinationId().getId());
			inner.setStatus(Aodv.DATA_PACKET_STATUS_ERROR);
			persistables.putAll(save());
		}

		return persistables;
	}

	public void setCurrentNode(Player player) {
		player.execute(new AsCurrent(inner));
	}
}
