package de.unipotsdam.nexplorer.server.hibernate;

import static org.junit.Assert.assertEquals;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unipotsdam.nexplorer.server.persistence.hibernate.HibernateSessions;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
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

	@After
	public void after() {
		Session session = HibernateSessions.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();

		Players p = (Players) session.createCriteria(Players.class).uniqueResult();
		session.delete(p);

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

	@Test
	public void shouldInsertMessageSilently() {
		Session session = HibernateSessions.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();

		Players p = (Players) session.createCriteria(Players.class).uniqueResult();
		AodvDataPackets packet = new AodvDataPackets();
		packet.setHopsDone((short) 3);
		packet.setPlayersByOwnerId(p);
		p.getAodvDataPacketsesForOwnerId().add(packet);

		session.flush();
		tx.commit();
		session.close();

		// Assert
		session = HibernateSessions.getSessionFactory().openSession();
		packet = (AodvDataPackets) session.createCriteria(AodvDataPackets.class).uniqueResult();
		short result = packet.getHopsDone();
		Long playerId = packet.getPlayersByOwnerId().getId();
		session.close();

		assertEquals(3, result);
		assertEquals(p.getId(), playerId);
	}
}
