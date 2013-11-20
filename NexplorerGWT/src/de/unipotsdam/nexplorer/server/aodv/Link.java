package de.unipotsdam.nexplorer.server.aodv;

import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.unipotsdam.nexplorer.server.PojoAction;
import de.unipotsdam.nexplorer.server.data.Maps;
import de.unipotsdam.nexplorer.server.data.Referee;
import de.unipotsdam.nexplorer.server.di.InjectLogger;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingMessages;
import de.unipotsdam.nexplorer.shared.Aodv;

public class Link {

	@InjectLogger
	private Logger logger;
	private AodvNode src;
	private AodvNode dest;
	private Locator locator;
	public AodvFactory factory;
	private Referee referee;

	@Inject
	public Link(@Assisted("from") AodvNode src, @Assisted("to") AodvNode dest, Locator locator, AodvFactory factory, Referee referee) {
		this.src = src;
		this.dest = dest;
		this.locator = locator;
		this.factory = factory;
		this.referee = referee;
	}

	public Map<Object, PojoAction> transmit(AodvDataPacket thePacket, Setting gameSettings) {
		Map<Object, PojoAction> persistables = Maps.empty();

		// prüfen ob Ziel wirklich noch in Reichweite und im Spiel
		if (locator.isInRange(src.player(), dest.player()) && dest.hasBattery()) {
			logger.trace("Datenpaket mit sourceId " + thePacket.inner().getPlayersBySourceId().getId() + " und destinationId " + thePacket.inner().getPlayersByDestinationId().getId() + " an Nachbarn mit ID " + dest.getId() + " senden, Batterie {} reduzieren.", src.getId());

			AodvDataPackets newPacket = new AodvDataPackets(thePacket.inner().getPlayersByDestinationId(), thePacket.inner().getPlayersByOwnerId(), thePacket.inner().getPlayersBySourceId(), thePacket.inner().getPlayersByCurrentNodeId(), thePacket.inner().getHopsDone(), thePacket.inner().getStatus(), thePacket.inner().getProcessingRound(), thePacket.inner().getDidReachBonusGoal());
			src.send(newPacket).toDestination();
			newPacket.setHopsDone((short) (newPacket.getHopsDone() + 1));
			newPacket.setProcessingRound(gameSettings.getCurrentDataRound() + 1);
			factory.create(newPacket).setCurrentNode(dest.player());
			// prüfen ob Paket Ziel erreicht hat
			if (dest.getId() == thePacket.inner().getPlayersByDestinationId().getId()) {
				AodvDataPacket arrivedPacket = factory.create(newPacket);
				logger.trace("Datenpaket mit sourceId " + arrivedPacket.inner().getPlayersBySourceId().getId() + " und destinationId " + arrivedPacket.inner().getPlayersByDestinationId().getId() + " hat sein Ziel erreicht.");
				arrivedPacket.inner().setStatus(Aodv.DATA_PACKET_STATUS_ARRIVED);
				persistables.putAll(referee.packetArrived(gameSettings, arrivedPacket));
			}
			persistables.put(newPacket, PojoAction.SAVE);

			src.player().increaseScoreBy(100);
			src.player().decreaseBatteryBy(.5);
			src.player().save();
		} else {
			logger.trace("Datenpaket mit sourceId " + thePacket.inner().getPlayersBySourceId().getId() + " und destinationId " + thePacket.inner().getPlayersByDestinationId().getId() + " konnte nicht an Nachbarn mit ID " + dest.getId() + " gesenden werden.");
		}

		return persistables;
	}

	public Map<Object, PojoAction> transmit(AodvRoutingMessages theError, Setting gameSettings) {
		Map<Object, PojoAction> persistables = Maps.empty();

		// prüfen ob Ziel wirklich noch in Reichweite und im Spiel
		if (locator.isInRange(src.player(), dest.player()) && dest.hasBattery()) {
			logger.trace("RERR mit sourceId " + theError.getSourceId() + " und destinationId " + theError.getDestinationId() + " an Nachbarn mit ID " + dest.getId() + " senden.\n");

			AodvRoutingMessages theNewRERR = new AodvRoutingMessages(theError.getCurrentNodeId(), theError.getType(), theError.getSourceId(), theError.getDestinationId(), null, theError.getSequenceNumber(), null, null, theError.getProcessingRound());
			theNewRERR.setCurrentNodeId(dest.player().getId());
			theNewRERR.setSourceId(src.getId());
			theNewRERR.setProcessingRound(gameSettings.getCurrentRoutingRound() + 1);
			theNewRERR.setHopCount(theError.getHopCount() == null ? null : theError.getHopCount() + 1);
			theNewRERR.setLifespan(theError.getLifespan() == null ? null : theError.getLifespan() - 1);

			PassedNodes nodes = new PassedNodes(theError.getPassedNodes());
			nodes.add(src.getId());
			theNewRERR.setPassedNodes(nodes.persistable());

			persistables.put(theNewRERR, PojoAction.SAVE);
		} else {
			logger.trace("RERR mit sourceId " + theError.getSourceId() + " und destinationId " + theError.getDestinationId() + " konnte nicht an Nachbarn mit ID " + dest.getId() + " gesenden werden.\n");
		}

		return persistables;
	}

	public Map<Object, PojoAction> transmit(AodvRoutingMessage theRequest, Setting gameSettings) {
		Map<Object, PojoAction> persistables = Maps.empty();

		// prüfen ob Ziel wirklich noch in Reichweite und im Spiel
		if (locator.isInRange(src.player(), dest.player()) && dest.hasBattery()) {
			// RREQ in Buffer eintragen
			Map<Object, PojoAction> result = src.addRouteRequestToBuffer(theRequest);
			persistables.putAll(result);

			logger.trace("RREQ mit sourceId " + theRequest.inner().getSourceId() + " und sequenceNumber " + theRequest.inner().getSequenceNumber() + " an Nachbarn mit ID " + dest.player().getId() + " senden.\n");

			AodvRoutingMessages theNewRREQ = new AodvRoutingMessages(theRequest.inner().getCurrentNodeId(), theRequest.inner().getType(), theRequest.inner().getSourceId(), theRequest.inner().getDestinationId(), theRequest.inner().getLifespan(), theRequest.inner().getSequenceNumber(), theRequest.inner().getHopCount(), theRequest.inner().getPassedNodes(), theRequest.inner().getProcessingRound());
			theNewRREQ.setCurrentNodeId(dest.player().getId());
			theNewRREQ.setLifespan(theNewRREQ.getLifespan() - 1);
			theNewRREQ.setHopCount(theNewRREQ.getHopCount() + 1);
			theNewRREQ.setProcessingRound(gameSettings.getCurrentRoutingRound() + 1);

			PassedNodes nodes = new PassedNodes(theNewRREQ.getPassedNodes());
			nodes.add(src.getId());
			theNewRREQ.setPassedNodes(nodes.persistable());

			persistables.put(theNewRREQ, PojoAction.SAVE);
		} else {
			logger.trace("RREQ mit sourceId " + theRequest.inner().getSourceId() + " und sequenceNumber " + theRequest.inner().getSequenceNumber() + " konnte nicht an Nachbarn mit ID " + dest.player().getId() + " gesenden werden.\n");
		}

		return persistables;
	}
}
