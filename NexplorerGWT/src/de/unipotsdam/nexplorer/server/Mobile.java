package de.unipotsdam.nexplorer.server;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unipotsdam.nexplorer.client.MobileService;
import de.unipotsdam.nexplorer.server.aodv.AodvRoutingAlgorithm;
import de.unipotsdam.nexplorer.server.data.ItemCollector;
import de.unipotsdam.nexplorer.server.data.NodeMapper;
import de.unipotsdam.nexplorer.server.data.PlayerDoesNotExistException;
import de.unipotsdam.nexplorer.server.data.Unit;
import de.unipotsdam.nexplorer.server.di.LogWrapper;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.PositionBacklog;
import de.unipotsdam.nexplorer.server.rest.dto.NodeGameSettingsJSON;
import de.unipotsdam.nexplorer.shared.Game;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.PlayerLocation;

/**
 * 
 * @author Julian Dehne and Hendrik Geßner
 * 
 */
@SuppressWarnings("serial")
public class Mobile extends RemoteServiceServlet implements MobileService {

	private Logger performance;

	public Mobile() {
		this.performance = LogManager.getLogger("performance");
	}

	/**
	 * collects and item which should be present in the db as well as shown in the gui. Is there any case where this might not be so?
	 */
	@Override
	public boolean collectItem(long playerId) {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			ItemCollector items = unit.resolve(ItemCollector.class);
			items.collectFor(playerId);
		} finally {
			unit.close();
		}

		long end = System.currentTimeMillis();
		performance.trace("collectItem took {}ms", end - begin);
		return true;
	}

	/**
	 * Be careful, aodv.updateNeighbourhood() is possible victim of race conditions
	 */
	@Override
	public boolean updateNeighbours(long playerId) {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			LogWrapper logger = unit.resolve(LogWrapper.class);
			logger.getLogger().trace("Updating neighbours of {}", playerId);

			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			// an dieser Stelle hatten wir vorgesehen, dass die Schnittstelle noch nicht vom Algorithmus abhängt
			// ist ein nice to have
			AodvRoutingAlgorithm aodv = unit.resolve(AodvRoutingAlgorithm.class);
			Player player = dbAccess.getPlayerById(playerId);
			aodv.updateNeighbourhood(player);
		} finally {
			unit.close();
		}

		long end = System.currentTimeMillis();
		performance.trace("updateNeighbours took {}ms", end - begin);
		return true;
	}

	@Override
	public boolean updatePlayerPosition(PlayerLocation location) {
		long begin = System.currentTimeMillis();

		blockingUpdate(location);

		long end = System.currentTimeMillis();
		performance.trace("updatePlayerPositions took {}ms", end - begin);
		return true;
	}

	private static synchronized void blockingUpdate(PlayerLocation location) {
		Unit unit = new Unit();
		try {
			LogWrapper logger = unit.resolve(LogWrapper.class);
			logger.getLogger().trace("Updating position of {}", location.getPlayerId());

			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			AodvRoutingAlgorithm aodv = unit.resolve(AodvRoutingAlgorithm.class);
			Player thePlayer = dbAccess.getPlayerById(location.getPlayerId());

			thePlayer.setLocation(location);
			thePlayer.save();

			PositionBacklog backlog = new PositionBacklog();
			backlog.setAccuracy(location.getAccuracy());
			backlog.setCreated(new Date().getTime());
			backlog.setHeading(location.getHeading());
			backlog.setPlayerId(location.getPlayerId());
			backlog.setLatitude(location.getLatitude());
			backlog.setLongitude(location.getLongitude());
			backlog.setSpeed(location.getSpeed());
			dbAccess.persist(backlog);

			// Wenn leichtester Schwierigkeitsgrad, Nachbarschaft aktualisieren
			if (thePlayer.getDifficulty() == Game.DIFFICULTY_EASY) {
				aodv.updateNeighbourhood(thePlayer);
			}
		} finally {
			unit.close();
		}
	}

	protected static synchronized void blockingNeighbourUpdate(Mobile instance) {
		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			List<Player> nodes = dbAccess.getAllActiveNodesInRandomOrder();
			for (Player node : nodes) {
				instance.updateNeighbours(node.getId());
			}
		} finally {
			unit.close();
		}
	}

	public NodeGameSettingsJSON getGameStatus(long id) throws PlayerDoesNotExistException {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			NodeMapper mapper = unit.resolve(NodeMapper.class);
			Players p = dbAccess.getRawById(id);

			Players node = mapper.from(p);
			GameStats stats = new GameStats(dbAccess.getSettings().inner());
			NodeGameSettingsJSON result = new NodeGameSettingsJSON(stats, node);
			return result;
		} finally {
			unit.close();

			long end = System.currentTimeMillis();
			performance.trace("getGameStatus took {}ms", end - begin);
		}
	}
}
