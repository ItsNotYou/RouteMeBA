package de.unipotsdam.nexplorer.server.aodv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Injector;

import de.unipotsdam.nexplorer.server.data.Referee;
import de.unipotsdam.nexplorer.server.di.GuiceFactory;
import de.unipotsdam.nexplorer.server.persistence.DataFactory;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Neighbours;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class AodvPingTest {

	private Players srcPlayer;
	private Players otherPlayer;
	private Players destPlayer;
	private DatabaseImpl dbAccess;
	private Injector injector;
	private DataFactory data;
	private Player src;
	private Player other;
	private Player dest;

	@Before
	public void setUp() {
		srcPlayer = new Players();
		srcPlayer.setNeighbourses(new HashSet<Neighbours>());

		otherPlayer = new Players();
		otherPlayer.setNeighbourses(new HashSet<Neighbours>());

		destPlayer = new Players();
		destPlayer.setNeighbourses(new HashSet<Neighbours>());
		destPlayer.getNeighbourses().add(new Neighbours(srcPlayer, destPlayer));

		dbAccess = mock(DatabaseImpl.class);

		injector = GuiceFactory.createInjector(dbAccess, new Locator(), new Referee(dbAccess));
		data = injector.getInstance(DataFactory.class);

		src = data.create(srcPlayer);
		other = data.create(otherPlayer);
		dest = data.create(destPlayer);
	}

	@Test
	public void testAddToNeighbours() {
		when(dbAccess.getNeighboursWithinRange(src)).thenReturn(Arrays.asList(other, dest));

		AodvNode sut = injector.getInstance(AodvFactory.class).create(src);
		sut.pingNeighbourhood();

		assertTrue(otherPlayer.getNeighbourses().contains(new Neighbours(srcPlayer, otherPlayer)));
		assertTrue(destPlayer.getNeighbourses().contains(new Neighbours(srcPlayer, destPlayer)));
		assertEquals(1, destPlayer.getNeighbourses().size());
		assertEquals(0, srcPlayer.getNeighbourses().size());
	}
}
