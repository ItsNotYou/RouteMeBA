package de.unipotsdam.nexplorer.server.persistence.hibernate;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unipotsdam.nexplorer.server.Admin;
import de.unipotsdam.nexplorer.server.data.Unit;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
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
