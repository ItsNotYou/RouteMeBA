package de.unipotsdam.nexplorer.tools.dummyplayer;

import static de.unipotsdam.nexplorer.shared.GameStatus.ISRUNNING;

import java.util.Random;
import java.util.UUID;

public class PlayerDummy extends Thread {

	private static String host = "127.0.0.1:8080";

	public static void main(String[] args) {
		String host = PlayerDummy.host;
		if (args.length > 0)
			host = args[0];

		System.out.println("Using host " + host);
		Connection connection = new Connection(host);

		String name = "Mobile-" + UUID.randomUUID().toString();
		connection.login(name);

		Settings status = null;
		boolean isRunning = false;
		while (!isRunning) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			status = connection.readGameStatus();
			isRunning = status.stats.settings.gameState == ISRUNNING;
		}

		StatsPrinter.startOnce();
		GameSettings gameField = status.stats.settings;
		PlayerDummy thread = new PlayerDummy(gameField.playingFieldUpperLeftLatitude, gameField.playingFieldUpperLeftLongitude, gameField.playingFieldLowerRightLatitude, gameField.playingFieldLowerRightLongitude, connection);
		thread.setPlayerInfo(status.node);

		thread.start();
		while (isRunning) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}

			status = connection.readGameStatus();
			thread.setPlayerInfo(status.node);
			isRunning = status.stats.settings.gameState == ISRUNNING;
		}
		thread.setIsRunning(false);
	}

	private boolean isRunning;
	private LocationGenerator locations;
	private Random random;
	private Connection connection;
	private PlayerInfo player;

	private PlayerDummy(double upperLeftLatitude, double upperLeftLongtiude, double lowerRightLatitude, double lowerRightLongitude, Connection connection) {
		this.isRunning = true;
		this.locations = new LocationGenerator(upperLeftLatitude, upperLeftLongtiude, lowerRightLatitude, lowerRightLongitude);
		this.random = new Random();
		this.connection = connection;
	}

	@Override
	public void run() {
		super.run();

		Location loc = locations.generateStartLocation(random.nextInt(360));
		while (isRunning) {
			// Send location and generate new one
			connection.sendLocation(loc);
			loc = locations.generateNextLocation(random.nextDouble() * 2, random.nextInt(91) - 45);

			for (int count = 0; count < 5; count++) {
				// Spam the item collect button if there is an item nearby
				if (player.nearbyItemsCount > 0) {
					connection.collectItem();
				}

				// Wait a little bit
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void setIsRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void setPlayerInfo(PlayerInfo player) {
		this.player = player;
	}
}
