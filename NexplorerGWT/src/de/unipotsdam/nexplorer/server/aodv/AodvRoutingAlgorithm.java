package de.unipotsdam.nexplorer.server.aodv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;

import de.unipotsdam.nexplorer.server.PojoAction;
import de.unipotsdam.nexplorer.server.data.Maps;
import de.unipotsdam.nexplorer.server.data.NeighbourAction;
import de.unipotsdam.nexplorer.server.data.PlayerDoesNotExistException;
import de.unipotsdam.nexplorer.server.di.InjectLogger;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Neighbour;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRouteRequestBufferEntries;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingMessages;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingTableEntries;
import de.unipotsdam.nexplorer.shared.Aodv;

public class AodvRoutingAlgorithm {

	@InjectLogger
	private Logger logger;
	private final AodvFactory factory;
	private final DatabaseImpl dbAccess;

	@Inject
	public AodvRoutingAlgorithm(AodvFactory factory, DatabaseImpl dbAccess, Locator locator) {
		this.factory = factory;
		this.dbAccess = dbAccess;
	}

	public Map<Object, PojoAction> aodvInsertNewMessage(Player src, Player dest, Player owner, List<AodvRoutingTableEntries> routingTable, Setting gameSettings) throws PlayerDoesNotExistException {
		logger.trace("Insert new message from {} to {} (owner {})", src.getId(), dest.getId(), owner.getId());
		AodvDataPackets newMessage = new AodvDataPackets();
		dest.execute(new AsDestination(newMessage));
		owner.execute(new AsOwner(newMessage));
		src.execute(new AsSource(newMessage));
		src.execute(new AsCurrent(newMessage));
		newMessage.setProcessingRound(gameSettings.getCurrentDataRound() + 1);
		newMessage.setHopsDone((short) 0);
		newMessage.setDidReachBonusGoal((byte) 0);

		return factory.create(src).enqueMessage(newMessage, routingTable, gameSettings);
	}

	public Map<Object, PojoAction> aodvResendRouteRequest(Player owner, Setting gameSettings, AodvDataPacket thePacket) {
		Map<Object, PojoAction> persistables = Maps.empty();

		if (thePacket == null) {
			logger.warn("Trying to resend route request, but no data packet found (owner {})", owner.getId());
			return Maps.empty();
		}

		AodvNode src = thePacket.getSource();
		AodvNode dest = thePacket.getDestination();

		logger.trace("Resend route request from {} to {} (owner {})", src.getId(), dest.getId(), owner.getId());
		persistables.putAll(thePacket.getSource().sendRREQToNeighbours(dest.player(), gameSettings));
		thePacket.inner().setStatus(Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE);
		persistables.putAll(thePacket.save());

		return persistables;
	}

	public Map<Object, PojoAction> aodvResetPlayerMessage(Player player, AodvDataPacket playerMessage) {
		Map<Object, PojoAction> persistables = Maps.empty();

		logger.trace("Reset player message {}", player.getId());
		if (playerMessage != null) {
			playerMessage.inner().setStatus(Aodv.DATA_PACKET_STATUS_CANCELLED);
			persistables.putAll(playerMessage.save());
		}

		return persistables;
	}

	public Map<Object, PojoAction> aodvProcessDataPackets(List<Player> allActiveNodeInRandomOrder, List<AodvRoutingTableEntries> routingTable, Setting gameSettings) {
		Map<Object, PojoAction> persistables = Maps.empty();

		List<AodvRoutingMessages> allRoutingMessages = dbAccess.getAllRoutingMessages();
		List<Player> allPlayers = dbAccess.getAllPlayers();
		List<Neighbour> allNeighbours = dbAccess.getAllNeighbours();

		logger.trace("------------adovProcessDataPackets Runde " + gameSettings.getCurrentRoutingRound() + " " + new SimpleDateFormat("dd.MM.yyyy HH:m:ss").format(new Date()) + "----------------");
		for (Player theNode : allActiveNodeInRandomOrder) {
			List<Neighbour> allKnownNeighbours = getAllNeighbours(theNode, allNeighbours);
			persistables.putAll(factory.create(theNode).aodvProcessDataPackets(gameSettings.getCurrentDataRound(), allKnownNeighbours, gameSettings.getCurrentRoutingRound(), routingTable, gameSettings, allRoutingMessages, allPlayers));
		}

		gameSettings.incCurrentDataRound();
		gameSettings.save();

		return persistables;
	}

	private List<Neighbour> getAllNeighbours(final Player theNode, List<Neighbour> allNeighbours) {
		Collection<Neighbour> result = Collections2.filter(allNeighbours, new Predicate<Neighbour>() {

			@Override
			public boolean apply(Neighbour arg0) {
				if (arg0.getNode().getId() == theNode.getId()) {
					return true;
				} else {
					return false;
				}
			}
		});

		return new ArrayList<Neighbour>(result);
	}

	public Map<Object, PojoAction> aodvProcessRoutingMessages(Setting gameSettings) {
		List<AodvRouteRequestBufferEntries> allRouteRequestBufferEntries = dbAccess.getAllRouteRequestBufferEntries();
		List<AodvRoutingTableEntries> allRoutingTableEntries = dbAccess.getAllRoutingTableEntries();
		List<Player> allActiveNodesInRandomOrder = dbAccess.getAllActiveNodesInRandomOrder();
		List<AodvRoutingMessage> allRoutingErrors = dbAccess.getRoutingErrors();
		List<AodvRoutingMessage> allRouteRequests = dbAccess.getRouteRequestsByRound();

		// alle Knoten bearbeiten welche noch im Spiel sind (zufällige Reihenfolge)
		logger.trace("------------adovProcessRoutingMessages Runde " + gameSettings.getCurrentDataRound() + " " + new SimpleDateFormat("dd.MM.yyyy HH:m:ss").format(new Date()) + "------------");

		Map<Object, PojoAction> persistables = Maps.empty();
		for (Player theNode : allActiveNodesInRandomOrder) {
			List<AodvRoutingMessage> nodeRERRs = getRoutingErrors(theNode, allRoutingErrors);
			List<AodvRoutingMessage> routeRequestsByNodeAndRound = getRouteRequestsByNodeAndRound(theNode, allRouteRequests);
			persistables.putAll(factory.create(theNode).aodvProcessRoutingMessages(this, nodeRERRs, routeRequestsByNodeAndRound, allRouteRequestBufferEntries, allRoutingTableEntries, gameSettings));
		}

		gameSettings.incCurrentRoutingRound();
		gameSettings.save();

		return persistables;
	}

	private List<AodvRoutingMessage> getRoutingErrors(final Player theNode, List<AodvRoutingMessage> allRoutingErrors) {
		Collection<AodvRoutingMessage> result = Collections2.filter(allRoutingErrors, new Predicate<AodvRoutingMessage>() {

			@Override
			public boolean apply(AodvRoutingMessage arg0) {
				if (arg0.inner().getCurrentNodeId() == theNode.getId()) {
					return true;
				} else {
					return false;
				}
			}
		});

		return new ArrayList<AodvRoutingMessage>(result);
	}

	private List<AodvRoutingMessage> getRouteRequestsByNodeAndRound(final Player theNode, List<AodvRoutingMessage> allRouteRequestsByRound) {
		Collection<AodvRoutingMessage> result = Collections2.filter(allRouteRequestsByRound, new Predicate<AodvRoutingMessage>() {

			@Override
			public boolean apply(AodvRoutingMessage arg0) {
				if (arg0.inner().getCurrentNodeId() == theNode.getId()) {
					return true;
				} else {
					return false;
				}
			}
		});

		return new ArrayList<AodvRoutingMessage>(result);
	}

	/**
	 * Be careful, possible race conditions ahead (if you're not careful enough)!
	 * 
	 * @param player
	 * @param routingTable
	 * @param allKnownNeighbours
	 *            TODO
	 * @return
	 */
	public Map<Object, PojoAction> updateNeighbourhood(Player player, long currentRoutingRound, List<AodvRoutingTableEntries> routingTable, Setting settings, List<Neighbour> allKnownNeighbours) {
		Map<Object, PojoAction> persistables = Maps.empty();
		NeighbourAction routing = factory.create(player);
		if (player.getDifficulty() == 1) {
			persistables.putAll(player.updateNeighbourhood(routing, allKnownNeighbours, currentRoutingRound, routingTable, settings));
		} else if (player.getDifficulty() == 2) {
			persistables.putAll(player.removeOutdatedNeighbours(routing, allKnownNeighbours, currentRoutingRound, routingTable, settings));
		}
		return persistables;
	}
}
