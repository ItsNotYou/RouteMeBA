package de.unipotsdam.nexplorer.server.di;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.unipotsdam.nexplorer.server.aodv.AodvFactory;
import de.unipotsdam.nexplorer.server.aodv.Locator;
import de.unipotsdam.nexplorer.server.data.Referee;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;

public class GuiceFactory {

	private static Injector instance;

	public static Injector getInstance() {
		if (instance == null) {
			createInstance();
		}
		return instance;
	}

	private synchronized static void createInstance() {
		if (instance == null) {
			instance = Guice.createInjector(new GlobalModule());
		}
	}

	public static Injector createInjector(DatabaseImpl dbAccess, Locator locator, Referee referee) {
		return Guice.createInjector(new GlobalModule(), new Testing(dbAccess, locator, referee));
	}

	public static Injector createInjector(AodvFactory factory, DatabaseImpl dbAccess) {
		return Guice.createInjector(new GlobalModule(), new BoundFactoryModule(factory, dbAccess));
	}
}
