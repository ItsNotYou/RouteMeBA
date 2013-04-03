package de.unipotsdam.nexplorer.server.time;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Injector;

import de.unipotsdam.nexplorer.server.di.GuiceFactory;

public class TimerTest {

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
