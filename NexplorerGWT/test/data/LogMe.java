package data;

import org.apache.logging.log4j.Logger;

import de.unipotsdam.nexplorer.server.di.InjectLogger;

public class LogMe {

	@InjectLogger
	private Logger logger;

	public boolean hasLogger() {
		return logger != null;
	}
}
