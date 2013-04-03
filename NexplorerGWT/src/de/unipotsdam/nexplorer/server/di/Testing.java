package de.unipotsdam.nexplorer.server.di;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import de.unipotsdam.nexplorer.server.aodv.AodvFactory;
import de.unipotsdam.nexplorer.server.aodv.Locator;
import de.unipotsdam.nexplorer.server.data.Referee;
import de.unipotsdam.nexplorer.server.persistence.DataFactory;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;

public class Testing extends AbstractModule {

	private DatabaseImpl dbAccess;
	private Locator locator;
	private Referee referee;

	Testing(DatabaseImpl dbAccess, Locator locator, Referee referee) {
		this.dbAccess = dbAccess;
		this.locator = locator;
		this.referee = referee;
	}

	@Override
	protected void configure() {
		bind(DatabaseImpl.class).toInstance(dbAccess);
		bind(Locator.class).toInstance(locator);
		bind(Referee.class).toInstance(referee);
		install(new FactoryModuleBuilder().build(AodvFactory.class));
		install(new FactoryModuleBuilder().build(DataFactory.class));
	}
}
