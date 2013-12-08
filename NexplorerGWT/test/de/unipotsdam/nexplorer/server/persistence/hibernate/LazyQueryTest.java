package de.unipotsdam.nexplorer.server.persistence.hibernate;

import static org.junit.Assert.assertEquals;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unipotsdam.nexplorer.server.aodv.AodvDataPacket;
import de.unipotsdam.nexplorer.server.data.Unit;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class LazyQueryTest {

	private static SessionFactory factory;
	private static Long ownerId;

	@BeforeClass
	public static void setUp() {
		factory = HibernateSessions.getSessionFactory();
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();

		Players current = new Players();
		current.setName("Current");
		session.save(current);

		Players owner = new Players();
		owner.setName("Owner");
		session.save(owner);

		Players dest = new Players();
		dest.setName("Dest");
		session.save(dest);

		Players source = new Players();
		source.setName("Source");
		session.save(source);

		AodvDataPackets packet = new AodvDataPackets();
		packet.setPlayersByCurrentNodeId(current);
		packet.setPlayersByDestinationId(dest);
		packet.setPlayersByOwnerId(owner);
		packet.setPlayersBySourceId(source);
		session.save(packet);

		t.commit();
		session.close();

		ownerId = owner.getId();
	}

	@AfterClass
	public static void tearDown() {
		HibernateSessions.clearDatabase();
	}

	@Test
	public void testRawEagerLoading() {
		Session session = factory.openSession();
		Criteria query = session.createCriteria(AodvDataPackets.class);
		query.setFetchMode("playersByCurrentNodeId", FetchMode.JOIN);
		AodvDataPackets packet = (AodvDataPackets) query.uniqueResult();
		session.close();

		String name = packet.getPlayersByCurrentNodeId().getName();
		assertEquals("Current", name);
	}

	@Test
	public void testManagedEagerLoading() {
		AodvDataPacket packet = null;
		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Player owner = dbAccess.getPlayerById(ownerId);
			packet = dbAccess.getDataPacketByOwnerId(owner);
		} finally {
			unit.close();
		}

		assertEquals("Current", packet.inner().getPlayersByCurrentNodeId().getName());
		assertEquals("Owner", packet.inner().getPlayersByOwnerId().getName());
		assertEquals("Source", packet.inner().getPlayersBySourceId().getName());
		assertEquals("Dest", packet.inner().getPlayersByDestinationId().getName());
	}
}
