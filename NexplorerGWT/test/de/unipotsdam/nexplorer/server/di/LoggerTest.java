package de.unipotsdam.nexplorer.server.di;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Injector;

import data.LogMe;
import data.Sub;

public class LoggerTest {

	private Injector injector;

	@Before
	public void before() {
		injector = GuiceFactory.getInstance();
	}

	@Test
	public void testLoggerInjection() {
		LogMe result = injector.getInstance(LogMe.class);
		assertTrue(result.hasLogger());
	}

	@Test
	public void testSuperclassLoggerInjection() {
		Sub result = injector.getInstance(Sub.class);
		assertTrue(result.hasLogger());
	}
}
