package de.unipotsdam.nexplorer.server.di;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import de.unipotsdam.nexplorer.server.aodv.AodvFactory;
import de.unipotsdam.nexplorer.server.persistence.DataFactory;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;

public class BoundFactoryModule extends AbstractModule {

	private DatabaseImpl dbAccess;
	private AodvFactory factory;

	public BoundFactoryModule(AodvFactory factory, DatabaseImpl dbAccess) {
		this.factory = factory;
		this.dbAccess = dbAccess;
	}

	@Override
	protected void configure() {
		bind(AodvFactory.class).toInstance(factory);
		bind(DatabaseImpl.class).toInstance(dbAccess);
		install(new FactoryModuleBuilder().build(DataFactory.class));
	}
}
