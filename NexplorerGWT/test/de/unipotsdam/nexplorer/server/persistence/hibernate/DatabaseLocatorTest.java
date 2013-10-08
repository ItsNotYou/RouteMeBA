package de.unipotsdam.nexplorer.server.persistence.hibernate;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unipotsdam.nexplorer.server.Admin;
import de.unipotsdam.nexplorer.server.data.Unit;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;

public class DatabaseLocatorTest {

	@BeforeClass
	public static void setUp() {
		Settings settings = new Admin().getDefaultGameStats();
		settings.setBaseNodeRange(9l);

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			dbAccess.persist(settings);

			Players players = new Players();
			players.setBattery(100d);
			players.setHasSignalRangeBooster(0l);
			players.setHasSignalStrengthBooster(null);
			Date now = new Date();
			players.setLastPositionUpdate(now.getTime());
			players.setLatitude(52.3935);
			players.setLongitude(13.130513);
			players.setName("testplayer");
			players.setRole((byte) 2);
			players.setScore(0l);
			players.setSequenceNumber(1l);
			players.setBaseNodeRange(settings.getBaseNodeRange());
			dbAccess.persist(players);

			Players two = new Players();
			two.setBattery(100.);
			two.setHasSignalRangeBooster(0l);
			two.setHasSignalStrengthBooster(null);
			two.setLastPositionUpdate(new Date().getTime());
			two.setLatitude(52.39345);
			two.setLongitude(13.130513);
			two.setName("player two");
			two.setRole((byte) 2);
			two.setScore(0l);
			two.setSequenceNumber(1l);
			two.setBaseNodeRange(settings.getBaseNodeRange());
			dbAccess.persist(two);

			Players third = new Players();
			third.setBattery(100.);
			third.setHasSignalRangeBooster(0l);
			third.setHasSignalStrengthBooster(0l);
			third.setLastPositionUpdate(new Date().getTime());
			third.setLatitude(52.3934);
			third.setLongitude(13.130513);
			third.setName("player three");
			third.setRole((byte) 2);
			third.setScore(0l);
			third.setSequenceNumber(15l);
			third.setBaseNodeRange(settings.getBaseNodeRange());
			dbAccess.persist(third);
		} finally {
			unit.close();
		}
	}

	private Unit unit;
	private DatabaseImpl dbAccess;

	@Before
	public void before() {
		this.unit = new Unit();
		this.dbAccess = unit.resolve(DatabaseImpl.class);
	}

	@After
	public void after() {
		this.unit.close();
	}

	@Test
	public void testAreWithinRange() {
		Player one = dbAccess.getPlayerById(1);

		List<Player> result = dbAccess.getNeighboursWithinRange(one);

		assertEquals(1, result.size());
	}
}
