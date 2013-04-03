package de.unipotsdam.nexplorer.server.aodv;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.shared.Locatable;

public class LocatorTest {

	@Test
	public void testDistance() {
		Locatable from = mock(Locatable.class);
		Player to = mock(Player.class);

		when(from.getLatitude()).thenReturn(52.123);
		when(from.getLongitude()).thenReturn(12.13);
		when(to.getLatitude()).thenReturn(52.123);
		when(to.getLongitude()).thenReturn(12.13);

		Locator sut = new Locator();
		double result = sut.distance(from, to);

		// Auf 25 cm genau
		assertEquals(0, result, .00025);
	}
}
