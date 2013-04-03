package de.unipotsdam.nexplorer.server.hibernate;

import static org.junit.Assert.assertEquals;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import de.unipotsdam.nexplorer.server.persistence.hibernate.HibernateSessions;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class SilentUpdateTest {

	@Before
	public void before() {
		Session session = HibernateSessions.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();

		Players p = new Players();
		p.setName("Hallo");
		session.save(p);

		session.flush();
		tx.commit();
		session.close();
	}

	@Test
	public void shouldUpdatePlayerSilently() {
		Session session = HibernateSessions.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();

		Players p = (Players) session.createCriteria(Players.class).uniqueResult();
		p.setName("Welt");

		session.flush();
		tx.commit();
		session.close();

		// Assert
		session = HibernateSessions.getSessionFactory().openSession();
		p = (Players) session.createCriteria(Players.class).uniqueResult();
		String result = p.getName();
		session.close();

		assertEquals("Welt", result);
	}
}
