package de.unipotsdam.nexplorer.server.data;

import java.io.Closeable;

import org.hibernate.Session;
import org.hibernate.Transaction;
import com.google.inject.Injector;

import de.unipotsdam.nexplorer.server.di.GuiceFactory;
import de.unipotsdam.nexplorer.server.di.SessionModule;
import de.unipotsdam.nexplorer.server.persistence.hibernate.HibernateSessions;

public class Unit implements Closeable {

	private final Session session;
	private final Injector injector;
	private final Transaction transaction;
	private boolean canceled;

	public Unit() {
		this.session = HibernateSessions.getSessionFactory().openSession();
		this.transaction = session.beginTransaction();
		this.injector = GuiceFactory.getInstance().createChildInjector(new SessionModule(session));
		
		this.canceled = false;
	}

	public <T> T resolve(Class<T> clazz) {
		return injector.getInstance(clazz);
	}

	@Override
	public void close() {
		try {
			if (canceled) {
				this.transaction.rollback();
			} else {
				this.session.flush();
				this.transaction.commit();
			}
		} catch (Exception e) {
			this.transaction.rollback();
		} finally {
			this.session.close();
		}
	}

	public void cancel() {
		this.canceled = true;
	}
}
