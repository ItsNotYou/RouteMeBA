package de.unipotsdam.nexplorer.server.di;

import org.hibernate.Session;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import de.unipotsdam.nexplorer.server.aodv.AodvFactory;
import de.unipotsdam.nexplorer.server.persistence.DataFactory;

public class SessionModule extends AbstractModule {

	private Session session;

	public SessionModule(Session session) {
		this.session = session;
	}

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().build(AodvFactory.class));
		install(new FactoryModuleBuilder().build(DataFactory.class));
		bind(Session.class).toInstance(session);
	}
}
