package de.unipotsdam.nexplorer.tools.dummyplayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LocationGeneratorTest {

	@Test
	public void shouldGenerateLocationInField() {
		LocationGenerator sut = new LocationGenerator(1, 5, 6, 10);
		Location result = sut.generateStartLocation(3);

		assertTrue(1 < result.getLatitude());
		assertTrue(result.getLatitude() < 6);
		assertTrue(5 < result.getLongitude());
		assertTrue(result.getLongitude() < 10);
	}

	@Test
	public void shouldSaveGeneratedLocation() {
		LocationGenerator sut = new LocationGenerator(1, 5, 6, 10);

		Location result = sut.generateStartLocation(3);
		Location result2 = sut.getLastLocation();

		assertTrue(result.equals(result2));
	}

	@Test
	public void shouldGenerateNewLocation() {
		LocationGenerator sut = new LocationGenerator(3, 1, 1, 3);
		sut.setStartDirection(2, 2, 0);

		Location result = sut.generateNextLocation(5, 0);

		assertEquals(2.000045, result.getLatitude(), 0.000005);
		assertEquals(2., result.getLongitude(), 0.000005);
	}

	@Test
	public void shouldGenerateDependingLocation() {
		LocationGenerator sut = new LocationGenerator(3, 1, 1, 3);
		sut.setStartDirection(2, 2, 0);
		sut.generateNextLocation(5, -90);

		Location result = sut.generateNextLocation(5, 90);

		assertEquals(2.000045, result.getLatitude(), 0.000005);
		assertEquals(2.000045, result.getLongitude(), 0.000005);
	}
}
