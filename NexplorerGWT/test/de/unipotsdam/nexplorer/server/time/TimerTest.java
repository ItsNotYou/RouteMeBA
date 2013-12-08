package de.unipotsdam.nexplorer.server.time;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Injector;

import de.unipotsdam.nexplorer.server.di.GuiceFactory;
import de.unipotsdam.nexplorer.server.persistence.hibernate.HibernateSessions;

public class TimerTest {

	@AfterClass
	public static void tearDown() {
		HibernateSessions.clearDatabase();
	}

	private Injector injector;
	private NeighbourUpdateStarter sut;

	@Before
	public void before() {
		injector = GuiceFactory.getInstance();
		sut = injector.getInstance(NeighbourUpdateStarter.class);
	}

	@Test
	public void shouldBeRestartable() {
		sut.pause();
		sut.resume();
		sut.pause();
		sut.resume();
	}
}
