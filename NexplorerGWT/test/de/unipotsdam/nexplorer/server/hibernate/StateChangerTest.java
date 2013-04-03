package de.unipotsdam.nexplorer.server.hibernate;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.unipotsdam.nexplorer.server.Admin;
import de.unipotsdam.nexplorer.server.rest.GameImpl;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.GameStatus;

public class StateChangerTest {

	@Test
	public void switchResumePaused() throws InterruptedException {
		Admin admin = new Admin();
		admin.startGame(new GameStats(admin.getDefaultGameStats()));
		GameImpl gameImpl = new GameImpl();
		for (int i = 0; i < 100; i++) {
			gameImpl.resumeGame();
			assertTrue(admin.getGameStats().getGameStatus().equals(GameStatus.ISRUNNING));
			gameImpl.pauseGame();
			assertTrue(admin.getGameStats().getGameStatus().equals(GameStatus.ISPAUSED));
		}
	}
}
