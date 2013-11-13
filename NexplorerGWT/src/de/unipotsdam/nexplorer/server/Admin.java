package de.unipotsdam.nexplorer.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unipotsdam.nexplorer.client.AdminService;
import de.unipotsdam.nexplorer.server.aodv.AodvRoutingAlgorithm;
import de.unipotsdam.nexplorer.server.data.ItemPlacer;
import de.unipotsdam.nexplorer.server.data.NodeMapper;
import de.unipotsdam.nexplorer.server.data.PlayerDoesNotExistException;
import de.unipotsdam.nexplorer.server.data.Unit;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Item;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.HibernateSessions;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;
import de.unipotsdam.nexplorer.server.time.Milliseconds;
import de.unipotsdam.nexplorer.server.time.ServerTimer;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.GameStatus;
import de.unipotsdam.nexplorer.shared.Messager;
import de.unipotsdam.nexplorer.shared.PlayerNotFoundException;
import de.unipotsdam.nexplorer.shared.PlayerStats;

@SuppressWarnings("serial")
public class Admin extends RemoteServiceServlet implements AdminService {

	public static final String PROPERTIES_PATH = "settings.xml";

	private Mobile mobile;
	private Logger performance;

	public Admin() {
		this.mobile = new Mobile();
		HibernateSessions.insertDebugPlayer();
		this.performance = LogManager.getLogger("performance");
	}

	@Override
	public boolean startGame(GameStats settings) {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);

			// Set values that are independent of parameter
			Settings gameSettings = new Settings();
			gameSettings.setGameState(GameStatus.ISPAUSED);
			gameSettings.setRunningSince(new Date().getTime());
			gameSettings.setLastPause(null);
			gameSettings.setPlayingTime(settings.getSettings().getPlayingTime() * 60 * 1000);
			gameSettings.setRemainingPlayingTime(gameSettings.getPlayingTime());

			gameSettings.setBonusGoal(null);
			gameSettings.setCurrentDataPacketProcessingRound(0l);
			gameSettings.setCurrentRoutingMessageProcessingRound(0l);

			// Copy parameter values
			Settings request = settings.getSettings();
			gameSettings.setBaseNodeRange(request.getBaseNodeRange());
			gameSettings.setDifficulty(request.getDifficulty());
			gameSettings.setItemCollectionRange(request.getItemCollectionRange());
			gameSettings.setMaxBatteries(request.getMaxBatteries());
			gameSettings.setMaxBoosters(request.getMaxBoosters());
			gameSettings.setPlayingFieldLowerRightLatitude(request.getPlayingFieldLowerRightLatitude());
			gameSettings.setPlayingFieldLowerRightLongitude(request.getPlayingFieldLowerRightLongitude());
			gameSettings.setPlayingFieldUpperLeftLatitude(request.getPlayingFieldUpperLeftLatitude());
			gameSettings.setPlayingFieldUpperLeftLongitude(request.getPlayingFieldUpperLeftLongitude());
			gameSettings.setProtocol(request.getProtocol());
			gameSettings.setUpdateDisplayIntervalTime(request.getUpdateDisplayIntervalTime());
			gameSettings.setUpdatePositionIntervalTime(request.getUpdatePositionIntervalTime());
			gameSettings.setPingDuration(request.getPingDuration());

			dbAccess.persist(gameSettings);
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();
		}

		unit = new Unit();
		try {
			ServerTimer timer = unit.resolve(ServerTimer.class);
			timer.pause();
		} catch (Exception e) {
			e.printStackTrace();
			unit.cancel();
		} finally {
			unit.close();
		}

		long end = System.currentTimeMillis();
		performance.trace("startGame took {}ms", end - begin);
		return true;
	}

	@Override
	public boolean stopGame() {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			unit.resolve(ServerTimer.class).stop();
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();
		}

		long end = System.currentTimeMillis();
		performance.trace("stopGame took {}ms", end - begin);
		return true;
	}

	@Override
	public boolean pauseGame() {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			unit.resolve(ServerTimer.class).pause();
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();
		}

		long end = System.currentTimeMillis();
		performance.trace("pauseGame took {}ms", end - begin);
		return true;
	}

	public boolean resumeGame() {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			unit.resolve(ServerTimer.class).resume();
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();
		}

		long end = System.currentTimeMillis();
		performance.trace("resumeGame took {}ms", end - begin);
		return true;
	}

	@Override
	public GameStats getGameStats() {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Setting gameSettings = dbAccess.getSettings();

			Settings settings;
			if (gameSettings != null) {
				settings = gameSettings.inner();
			} else {
				settings = new Settings();
				settings.setGameState(GameStatus.NOTSTARTED);
			}

			GameStats stats = new GameStats(settings);
			return stats;
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();

			long end = System.currentTimeMillis();
			performance.trace("getGameStats took {}ms", end - begin);
		}
	}

	@Override
	public List<Items> getItemStats() {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			List<Items> stats = new ArrayList<Items>();
			for (Item item : dbAccess.getAllItems()) {
				stats.add(item.inner());
			}
			return stats;
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();

			long end = System.currentTimeMillis();
			performance.trace("getItemStats took {}ms", end - begin);
		}
	}

	@Override
	public PlayerStats getPlayerStats() throws PlayerNotFoundException {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			NodeMapper mapper = unit.resolve(NodeMapper.class);
			List<Messager> messagers = dbAccess.getAllIndoors();
			List<Player> playerNodes = dbAccess.getAllNodesByScoreDescending();

			List<Players> nodes = new LinkedList<Players>();
			for (Player player : playerNodes) {
				Mapping mapping = new Mapping(mapper);
				try {
					player.execute(mapping);
				} catch (PlayerDoesNotExistException e) {
					throw new PlayerNotFoundException(e);
				}
				nodes.add(mapping.getMapping());
			}

			PlayerStats stats = new PlayerStats(messagers, nodes);
			return stats;
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();

			long end = System.currentTimeMillis();
			performance.trace("getPlayerStats took {}ms", end - begin);
		}
	}

	public void aodvProcessDataPackets() {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			AodvRoutingAlgorithm aodv = unit.resolve(AodvRoutingAlgorithm.class);
			aodv.aodvProcessDataPackets();
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();

			long end = System.currentTimeMillis();
			performance.trace("aodvProcessDataPackets took {}ms", end - begin);
		}
	}

	public void aodvProcessRoutingMessages() {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			AodvRoutingAlgorithm aodv = unit.resolve(AodvRoutingAlgorithm.class);
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Map<Object, PojoAction> result = aodv.aodvProcessRoutingMessages();
			for (Map.Entry<Object, PojoAction> persistable : result.entrySet()) {
				switch (persistable.getValue()) {
				case DELETE:
					dbAccess.deleteObject(persistable);
				case SAVE:
				default:
					dbAccess.persistObject(persistable);
				}
			}
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();

			long end = System.currentTimeMillis();
			performance.trace("aodvProcessRoutingMessages took {}ms", end - begin);
		}
	}

	public boolean placeItems() {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			ItemPlacer placer = new ItemPlacer(dbAccess);
			placer.placeItems();
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();
		}

		long end = System.currentTimeMillis();
		performance.trace("placeItems took {}ms", end - begin);
		return true;
	}

	public boolean resetGame() {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			dbAccess.resetDatabase();
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();
		}

		long end = System.currentTimeMillis();
		performance.trace("resetGame took {}ms", end - begin);
		return true;
	}

	public boolean updateBonusGoals() {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Setting settings = dbAccess.getSettings();
			settings.findNewBonusGoal();
			settings.save();
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();
		}

		long end = System.currentTimeMillis();
		performance.trace("updateBonusGoals took {}ms", end - begin);
		return true;
	}

	public boolean updateNodeBatteries() {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Setting gameSettings = dbAccess.getSettings();

			if (gameSettings.inner().getDidEnd() == 0 && gameSettings.inner().getIsRunning() == 1) {
				for (Player theNode : dbAccess.getAllActiveNodesInRandomOrder()) {
					double malus = 3 + theNode.getNeighbours().size() * 0.5;
					theNode.decreaseBatteryBy(malus);
					// theNode.save();

					if (theNode.hasBattery()) {
						theNode.increaseScoreBy(theNode.getNeighbours().size() * 10);
						theNode.save();
					} else {
						// aufräumen wenn Knoten ausgefallen (AODV)
						dbAccess.cleanRoutingEntriesFor(theNode);
					}
				}

				// Spielende wenn nur noch ein Knoten übrig
				if (dbAccess.getAllActiveNodesInRandomOrder().size() <= 1) {
					stopGame();
				}
			}
		} catch (Exception e) {
			unit.cancel();
			System.err.println(e.getMessage());
		} finally {
			unit.close();
		}

		long end = System.currentTimeMillis();
		performance.trace("updateNodeBatteries took {}ms", end - begin);
		return true;
	}

	public boolean updateRemainingPlayingTime(Milliseconds delta) {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			Setting settings = unit.resolve(DatabaseImpl.class).getSettings();

			settings.decreasePlayingTimeBy(delta);
			if (settings.noTimeLeft()) {
				stopGame();
			}

			settings.save();
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();
		}

		long end = System.currentTimeMillis();
		performance.trace("updateRemainingPlayingTime took {}ms", end - begin);
		return true;
	}

	private static String findSettingsPath() throws UnsupportedEncodingException {
		URL ClassUrl = Admin.class.getProtectionDomain().getCodeSource().getLocation();
		String jarURL = ClassUrl.getFile();
		jarURL = jarURL.replace(Admin.class.getCanonicalName(), "");
		jarURL = URLDecoder.decode(jarURL, "UTF-8");

		File currentDir = new File(jarURL);
		while (!currentDir.getName().equals("WEB-INF")) {
			currentDir = currentDir.getParentFile();
		}

		return currentDir.getAbsolutePath() + "/" + PROPERTIES_PATH;
	}

	@Override
	public Settings getDefaultGameStats() {
		long begin = System.currentTimeMillis();

		FileInputStream fileInput = null;
		FileOutputStream fileOutputStream = null;
		Properties props = new Properties();
		try {
			fileInput = new FileInputStream(new File(findSettingsPath()));
			props.loadFromXML(fileInput);
			if (!props.containsKey("playingTime")) {
				props.setProperty("playingTime", "10");
				props.setProperty("baseNodeRange", "9");
				props.setProperty("itemCollectionRange", "4");
				props.setProperty("numberOfBatteries", "5");
				props.setProperty("numberOfBoosters", "2");
				props.setProperty("difficulty", "1");
				props.setProperty("protocol", "aodv");
				props.setProperty("playingFieldUpperLeftLatitude", "52.39235518805976");
				props.setProperty("playingFieldUpperLeftLongitude", "13.13081675662238");
				props.setProperty("playingFieldLowerRightLatitude", "52.39209820651062");
				props.setProperty("playingFieldLowerRightLongitude", "13.131460486785954");
				props.setProperty("updateDisplayIntervalTime", "1000");
				props.setProperty("updatePositionIntervalTime", "1000");
				props.setProperty("pingDuration", "1000");
				fileOutputStream = new FileOutputStream(new File(findSettingsPath()));
				props.storeToXML(fileOutputStream, null);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fileInput != null) {
				try {
					fileInput.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		Long playingTime = Long.parseLong(props.getProperty("playingTime"));
		Long baseNodeRange = Long.parseLong(props.getProperty("baseNodeRange"));
		Long itemCollectionRange = Long.parseLong(props.getProperty("itemCollectionRange"));
		Long maxBatteries = Long.parseLong(props.getProperty("numberOfBatteries"));
		Long maxBoosters = Long.parseLong(props.getProperty("numberOfBoosters"));
		Long difficulty = Long.parseLong(props.getProperty("difficulty"));
		String protocol = props.getProperty("protocol");
		Double playingFieldUpperLeftLatitude = Double.parseDouble(props.getProperty("playingFieldUpperLeftLatitude"));
		Double playingFieldUpperLeftLongitude = Double.parseDouble(props.getProperty("playingFieldUpperLeftLongitude"));
		Double playingFieldLowerRightLatitude = Double.parseDouble(props.getProperty("playingFieldLowerRightLatitude"));
		Double playingFieldLowerRightLongitude = Double.parseDouble(props.getProperty("playingFieldLowerRightLongitude"));
		long pingDuration = Long.parseLong(props.getProperty("pingDuration"));
		Settings settingsGWT = new Settings();
		settingsGWT.setProtocol(protocol);
		settingsGWT.setBaseNodeRange(baseNodeRange);
		settingsGWT.setDifficulty(difficulty);
		settingsGWT.setPlayingTime(playingTime);
		settingsGWT.setMaxBatteries(maxBatteries);
		settingsGWT.setMaxBoosters(maxBoosters);
		settingsGWT.setItemCollectionRange(itemCollectionRange);
		settingsGWT.setPlayingFieldUpperLeftLatitude(playingFieldUpperLeftLatitude);
		settingsGWT.setPlayingFieldUpperLeftLongitude(playingFieldUpperLeftLongitude);
		settingsGWT.setPlayingFieldLowerRightLatitude(playingFieldLowerRightLatitude);
		settingsGWT.setPlayingFieldLowerRightLongitude(playingFieldLowerRightLongitude);
		settingsGWT.updateDisplayIntervalTime = Long.parseLong(props.getProperty("updateDisplayIntervalTime"));
		settingsGWT.updatePositionIntervalTime = Long.parseLong(props.getProperty("updatePositionIntervalTime"));
		settingsGWT.setPingDuration(pingDuration);

		long end = System.currentTimeMillis();
		performance.trace("getDefaultGameStats took {}ms", end - begin);
		return settingsGWT;
	}

	public void updateNeighbours() {
		long begin = System.currentTimeMillis();

		Mobile.blockingNeighbourUpdate(mobile);

		long end = System.currentTimeMillis();
		performance.trace("updateNeighbours took {}ms", end - begin);
	}
}
