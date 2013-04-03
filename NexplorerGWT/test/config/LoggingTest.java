package config;

import static org.junit.Assert.assertNotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;

public class LoggingTest {

	@Test
	public void testLoggerCreation() {
		Logger sut = LogManager.getLogger(DatabaseImpl.class);
		assertNotNull(sut);
	}
}
