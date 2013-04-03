package de.unipotsdam.nexplorer.server.di;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

import de.unipotsdam.nexplorer.server.time.ServerTimer;

public class GlobalModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ServerTimer.class).asEagerSingleton();
		bindListener(Matchers.any(), new Log4JTypeListener());
	}
}
