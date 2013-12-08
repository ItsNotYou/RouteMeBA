package de.unipotsdam.nexplorer.server.persistence.hibernate;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
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

		Players one = null;
		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			dbAccess.persist(settings);

			one = new Players();
			one.setBattery(100d);
			one.setHasSignalRangeBooster(0l);
			one.setHasSignalStrengthBooster(null);
			Date now = new Date();
			one.setLastPositionUpdate(now.getTime());
			one.setLatitude(52.3935);
			one.setLongitude(13.130513);
			one.setName("testplayer");
			one.setRole((byte) 2);
			one.setScore(0l);
			one.setSequenceNumber(1l);
			one.setBaseNodeRange(settings.getBaseNodeRange());
			dbAccess.persist(one);

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

		firstId = one.getId();
	}

	@AfterClass
	public static void tearDown() {
		HibernateSessions.clearDatabase();
	}

	private static long firstId;
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
		Player one = dbAccess.getPlayerById(firstId);

		List<Player> result = dbAccess.getNeighboursWithinRange(one);

		assertEquals(1, result.size());
	}
}
