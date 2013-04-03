package de.unipotsdam.nexplorer.server.di;

import org.apache.logging.log4j.Logger;

public class LogWrapper {

	@InjectLogger
	private Logger logger;

	public Logger getLogger() {
		return logger;
	}
}
