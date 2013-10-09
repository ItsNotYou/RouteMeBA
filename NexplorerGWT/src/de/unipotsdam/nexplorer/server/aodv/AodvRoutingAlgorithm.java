package de.unipotsdam.nexplorer.server.aodv;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

import de.unipotsdam.nexplorer.server.data.NeighbourAction;
import de.unipotsdam.nexplorer.server.data.PlayerDoesNotExistException;
import de.unipotsdam.nexplorer.server.di.InjectLogger;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.shared.Aodv;

public class AodvRoutingAlgorithm {

	@InjectLogger
	private Logger logger;
	private final AodvFactory factory;
	private final DatabaseImpl dbAccess;
	private Setting settings;

	@Inject
	public AodvRoutingAlgorithm(AodvFactory factory, DatabaseImpl dbAccess, Locator locator) {
		this.factory = factory;
		this.dbAccess = dbAccess;
		this.settings = null;
	}

	public Collection<Object> aodvInsertNewMessage(Player src, Player dest, Player owner) throws PlayerDoesNotExistException {
		Setting gameSettings = getGameSettings();
		logger.trace("Insert new message from {} to {} (owner {})", src.getId(), dest.getId(), owner.getId());
		AodvDataPackets newMessage = new AodvDataPackets();
		dest.execute(new AsDestination(newMessage));
		owner.execute(new AsOwner(newMessage));
		src.execute(new AsSource(newMessage));
		src.execute(new AsCurrent(newMessage));
		newMessage.setProcessingRound(gameSettings.getCurrentDataRound() + 1);
		newMessage.setHopsDone((short) 0);
		newMessage.setDidReachBonusGoal((byte) 0);
		return factory.create(src).enqueMessage(newMessage);
	}

	public void aodvResendRouteRequest(Player owner) {
		AodvDataPacket thePacket = dbAccess.getDataPacketByOwnerId(owner);
		if (thePacket == null) {
			logger.warn("Trying to resend route request, but no data packet found (owner {})", owner.getId());
			return;
		}

		AodvNode src = thePacket.getSource();
		AodvNode dest = thePacket.getDestination();

		logger.trace("Resend route request from {} to {} (owner {})", src.getId(), dest.getId(), owner.getId());
		thePacket.getSource().sendRREQToNeighbours(dest.player());
		thePacket.inner().setStatus(Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE);
		thePacket.save();
	}

	public void aodvResetPlayerMessage(Player player) {
		logger.trace("Reset player message {}", player.getId());
		AodvDataPacket playerMessage = dbAccess.getDataPacketByOwnerId(player);
		if (playerMessage != null) {
			playerMessage.inner().setStatus(Aodv.DATA_PACKET_STATUS_CANCELLED);
			playerMessage.save();
		}
	}

	public void aodvProcessDataPackets() {
		Setting gameSettings = getGameSettings();
		logger.trace("------------adovProcessDataPackets Runde " + gameSettings.getCurrentRoutingRound() + " " + new SimpleDateFormat("dd.MM.yyyy HH:m:ss").format(new Date()) + "----------------");
		for (Player theNode : dbAccess.getAllActiveNodesInRandomOrder()) {
			factory.create(theNode).aodvProcessDataPackets(gameSettings.getCurrentDataRound());
		}

		gameSettings.incCurrentDataRound();
		gameSettings.save();
	}

	public void aodvProcessRoutingMessages() {
		Setting gameSettings = getGameSettings();
		// alle Knoten bearbeiten welche noch im Spiel sind (zufällige Reihenfolge)
		logger.trace("------------adovProcessRoutingMessages Runde " + gameSettings.getCurrentDataRound() + " " + new SimpleDateFormat("dd.MM.yyyy HH:m:ss").format(new Date()) + "------------");
		for (Player theNode : dbAccess.getAllActiveNodesInRandomOrder()) {
			factory.create(theNode).aodvProcessRoutingMessages(this);
		}

		gameSettings.incCurrentRoutingRound();
		gameSettings.save();
	}

	/**
	 * Be careful, possible race conditions ahead (if you're not careful enough)!
	 * 
	 * @param player
	 */
	public void updateNeighbourhood(Player player) {
		NeighbourAction routing = factory.create(player);
		if (player.getDifficulty() == 1) {
			player.updateNeighbourhood(routing);
		} else if (player.getDifficulty() == 2) {
			player.removeOutdatedNeighbours(routing);
		}
	}

	private Setting getGameSettings() {
		if (settings == null) {
			settings = dbAccess.getSettings();
		}
		return settings;
	}
}
