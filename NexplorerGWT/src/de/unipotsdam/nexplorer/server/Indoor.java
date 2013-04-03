package de.unipotsdam.nexplorer.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unipotsdam.nexplorer.client.IndoorService;
import de.unipotsdam.nexplorer.server.aodv.AodvDataPacket;
import de.unipotsdam.nexplorer.server.aodv.AodvRoutingAlgorithm;
import de.unipotsdam.nexplorer.server.data.GetInternal;
import de.unipotsdam.nexplorer.server.data.NodeMapper;
import de.unipotsdam.nexplorer.server.data.PlayerDoesNotExistException;
import de.unipotsdam.nexplorer.server.data.Unit;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.server.rest.dto.MarkersJSON;
import de.unipotsdam.nexplorer.shared.DataPacketLocatable;
import de.unipotsdam.nexplorer.shared.MessageDescription;
import de.unipotsdam.nexplorer.shared.PlayerInfo;
import de.unipotsdam.nexplorer.shared.PlayerNotFoundException;

@SuppressWarnings("serial")
public class Indoor extends RemoteServiceServlet implements IndoorService {

	private Logger performance;

	public Indoor() {
		this.performance = LogManager.getLogger("performance");
	}

	@Override
	public PlayerInfo getPlayerInfo(int playerId) throws PlayerNotFoundException {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Setting settings = dbAccess.getSettings();
			if (settings == null) {
				log("settings ist null auf Serverseite");
				return new PlayerInfo();
			}
			if (playerId == -1) {
				return new PlayerInfo(null, settings.inner(), null);
			} else {
				Player player = dbAccess.getPlayerById(playerId);
				AodvDataPacket packet = dbAccess.getDataPacketByOwnerId(player);

				AodvDataPackets transmittablePacket = packet != null ? packet.inner() : null;
				GetInternal internal = new GetInternal();
				try {
					player.execute(internal);
				} catch (PlayerDoesNotExistException e) {
					throw new PlayerNotFoundException(e);
				}
				return new PlayerInfo(internal.get(), settings.inner(), transmittablePacket);
			}
		} finally {
			unit.close();

			long end = System.currentTimeMillis();
			performance.trace("getPlayerInfo took {}ms", end - begin);
		}
	}

	@Override
	public boolean insertNewMessage(MessageDescription request) throws PlayerNotFoundException {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);

			Player src = dbAccess.getPlayerById(request.getSourceNodeId());
			Player dest = dbAccess.getPlayerById(request.getDestinationNodeId());
			Player owner = dbAccess.getPlayerById(request.getOwnerId());

			AodvRoutingAlgorithm aodv = unit.resolve(AodvRoutingAlgorithm.class);
			try {
				aodv.aodvInsertNewMessage(src, dest, owner);
			} catch (PlayerDoesNotExistException e) {
				throw new PlayerNotFoundException(e);
			}
			return true;
		} finally {
			unit.close();

			long end = System.currentTimeMillis();
			performance.trace("getPlayerInfo took {}ms", end - begin);
		}
	}

	@Override
	public boolean resendRouteRequest(long playerId) {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Player owner = dbAccess.getPlayerById(playerId);

			AodvRoutingAlgorithm aodv = unit.resolve(AodvRoutingAlgorithm.class);
			aodv.aodvResendRouteRequest(owner);
		} finally {
			unit.close();
		}

		long end = System.currentTimeMillis();
		performance.trace("getPlayerInfo took {}ms", end - begin);
		return true;
	}
	
	@Override
	public boolean policyDummy(MessageDescription desc) {
		return true;
	}

	public boolean resetPlayerMessage(long playerId) {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Player player = dbAccess.getPlayerById(playerId);

			AodvRoutingAlgorithm aodv = unit.resolve(AodvRoutingAlgorithm.class);
			aodv.aodvResetPlayerMessage(player);
		} finally {
			unit.close();
		}

		long end = System.currentTimeMillis();
		performance.trace("getPlayerInfo took {}ms", end - begin);
		return true;
	}

	public MarkersJSON getMarkers() throws PlayerDoesNotExistException {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			NodeMapper mapper = unit.resolve(NodeMapper.class);
			List<Player> nodes = dbAccess.getAllActiveNodesInRandomOrder();
			List<AodvDataPacket> packets = new LinkedList<AodvDataPacket>();

			HashMap<Long, Players> jsonNodes = new HashMap<Long, Players>();
			for (Player node : nodes) {
				Players jsonNode = mapper.toJSON(node);
				jsonNodes.put(jsonNode.getId(), jsonNode);
				packets.addAll(dbAccess.getAllDataPacketsSortedByDate(node));
			}

			HashMap<Long, DataPacketLocatable> jsonPackets = new HashMap<Long, DataPacketLocatable>();
			for (AodvDataPacket packet : packets) {
				DataPacketLocatable jsonPacket = mapper.toJSON(packet);
				jsonPackets.put(jsonPacket.getId(), jsonPacket);
			}

			return new MarkersJSON(jsonNodes, jsonPackets);
		} finally {
			unit.close();

			long end = System.currentTimeMillis();
			performance.trace("getPlayerInfo took {}ms", end - begin);
		}
	}

	@Override
	public int getUpdateDisplayFrequency() {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Setting settings = dbAccess.getSettings();
			return settings.inner().getUpdateDisplayIntervalTime().intValue();
		} finally {
			unit.close();

			long end = System.currentTimeMillis();
			performance.trace("getPlayerInfo took {}ms", end - begin);
		}
	}
}
