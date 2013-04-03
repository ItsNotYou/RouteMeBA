package de.unipotsdam.nexplorer.server.di;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.MembersInjector;

public class Log4JMembersInjector<T> implements MembersInjector<T> {

	private final Field field;
	private final Logger logger;

	public Log4JMembersInjector(Field field, Class<? super T> rawType) {
		this.field = field;
		this.logger = LogManager.getLogger(rawType);
		field.setAccessible(true);
	}

	public void injectMembers(T t) {
		try {
			field.set(t, logger);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
