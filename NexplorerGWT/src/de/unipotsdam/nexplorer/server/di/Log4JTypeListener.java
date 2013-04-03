package de.unipotsdam.nexplorer.server.di;

import java.lang.reflect.Field;

import org.apache.logging.log4j.Logger;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class Log4JTypeListener implements TypeListener {

	@Override
	public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
		Class<? super I> type = typeLiteral.getRawType();
		do {
			// Search all declared fields for a logger
			for (Field field : type.getDeclaredFields()) {
				if (field.getType() == Logger.class && field.isAnnotationPresent(InjectLogger.class)) {
					// Logger field found. Register a logger injector
					typeEncounter.register(new Log4JMembersInjector<I>(field, typeLiteral.getRawType()));
				}
			}

			// Walk the class hierarchy upwards
			type = type.getSuperclass();
		} while (type != null);
	}
}
