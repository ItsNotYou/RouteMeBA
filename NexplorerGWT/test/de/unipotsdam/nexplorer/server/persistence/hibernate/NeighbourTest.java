package de.unipotsdam.nexplorer.server.persistence.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.AfterClass;
import org.junit.Test;

import de.unipotsdam.nexplorer.server.data.Unit;
import de.unipotsdam.nexplorer.server.persistence.DataFactory;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.NeighbourSet;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Neighbours;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class NeighbourTest {

	@AfterClass
	public static void tearDown() {
		HibernateSessions.clearDatabase();
	}

	@Test
	public void shouldHaveNewNeighbour() {
		Unit unit = new Unit();
		DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
		Players other = new Players();
		dbAccess.persist(other);
		unit.close();

		unit = new Unit();
		dbAccess = unit.resolve(DatabaseImpl.class);
		Players sut = new Players();
		dbAccess.persist(sut);
		unit.close();

		unit = new Unit();
		dbAccess = unit.resolve(DatabaseImpl.class);
		sut = dbAccess.getRawById(sut.getId());
		Neighbours neigh = new Neighbours();
		neigh.setNode(sut);
		neigh.setNeighbour(other);
		sut.getNeighbourses().add(neigh);
		dbAccess.persist(sut);
		unit.close();

		unit = new Unit();
		try {
			dbAccess = unit.resolve(DatabaseImpl.class);
			sut = dbAccess.getRawById(sut.getId());
			other = dbAccess.getRawById(other.getId());

			assertEquals(1, sut.getNeighbourses().size());
			assertEquals(0, other.getNeighbourses().size());
		} finally {
			unit.close();
		}
	}

	@Test
	public void uniOrBicorn() {
		Unit unit = new Unit();
		DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);

		Players p = new Players();
		Neighbours n = new Neighbours();
		n.setNode(p);
		n.setNeighbour(p);
		p.setNeighbourses(new HashSet<Neighbours>());
		p.getNeighbourses().add(n);

		dbAccess.persist(p);
		unit.close();

		unit = new Unit();
		try {
			dbAccess = unit.resolve(DatabaseImpl.class);
			Players sut = dbAccess.getRawById(p.getId());

			assertEquals(1, sut.getNeighbourses().size());
		} finally {
			unit.close();
		}
	}

	@Test
	public void deleteNeighbourFromRel() {
		Unit unit = new Unit();
		DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);

		Players p = new Players();
		Neighbours n = new Neighbours();
		n.setNode(p);
		n.setNeighbour(p);
		p.setNeighbourses(new HashSet<Neighbours>());
		p.getNeighbourses().add(n);

		dbAccess.persist(p);
		unit.close();

		// delete
		Unit unit2 = new Unit();
		dbAccess = unit2.resolve(DatabaseImpl.class);
		Players players2 = dbAccess.getRawById(p.getId());
		assertNotNull(players2);
		assertNotNull(players2.getNeighbourses());
		assertFalse(players2.getNeighbourses().isEmpty());
		Neighbours neighbours = players2.getNeighbourses().iterator().next();
		assertNotNull(neighbours);
		// neighbours.setPlayers(null);
		players2.getNeighbourses().remove(neighbours);
		// dbAccess.persist(neighbours);
		dbAccess.persist(players2);
		dbAccess.delete(neighbours);
		unit2.close();

		// assert
		Unit unit3 = new Unit();
		dbAccess = unit3.resolve(DatabaseImpl.class);
		Players players3 = dbAccess.getRawById(p.getId());
		assertTrue(players3.getNeighbourses().isEmpty());
		unit3.close();
	}

	@Test
	public void shouldDeleteNeighbour() {
		Unit unit = new Unit();
		DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
		Players one = new Players();
		Players two = new Players();
		Neighbours neigh = new Neighbours();
		neigh.setNode(one);
		neigh.setNeighbour(two);
		dbAccess.persist(one);
		dbAccess.persist(two);
		dbAccess.persist(neigh);
		unit.close();

		unit = new Unit();
		dbAccess = unit.resolve(DatabaseImpl.class);
		DataFactory factory = unit.resolve(DataFactory.class);
		Players inner = dbAccess.getRawById(one.getId());

		NeighbourSet sut = new NeighbourSet(inner, dbAccess, factory);
		Player first = sut.iterator().next();
		sut.remove(first);
		unit.close();

		unit = new Unit();
		dbAccess = unit.resolve(DatabaseImpl.class);
		Player result = dbAccess.getPlayerById(one.getId());
		int size = result.getNeighbours().size();
		unit.close();

		assertEquals(0, size);
	}
}
