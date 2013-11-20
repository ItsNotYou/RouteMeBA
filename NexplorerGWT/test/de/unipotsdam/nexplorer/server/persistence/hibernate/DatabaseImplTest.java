package de.unipotsdam.nexplorer.server.persistence.hibernate;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unipotsdam.nexplorer.server.aodv.AodvFactory;
import de.unipotsdam.nexplorer.server.aodv.AodvRoutingMessage;
import de.unipotsdam.nexplorer.server.data.Unit;
import de.unipotsdam.nexplorer.server.persistence.DataFactory;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.Role;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingMessages;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;
import de.unipotsdam.nexplorer.shared.GameStatus;
import de.unipotsdam.nexplorer.shared.Location;

public class DatabaseImplTest {

	private Unit unit;
	private DatabaseImpl dbAccess;
	private DataFactory data;
	private AodvFactory aodv;

	@BeforeClass
	public static void setUp() {
		HibernateSessions.forceNewSessionFactory();

		Settings settings = new Settings();
		settings.setBaseNodeRange(3l);
		settings.setCurrentDataPacketProcessingRound(1l);
		settings.setCurrentRoutingMessageProcessingRound(1l);
		settings.setDifficulty(1l);
		settings.setGameState(GameStatus.ISRUNNING);
		settings.setPlayingFieldLowerRightLatitude(13.);
		settings.setPlayingFieldLowerRightLongitude(52.);
		settings.setPlayingFieldUpperLeftLatitude(12.);
		settings.setPlayingFieldUpperLeftLongitude(51.);

		Players p = new Players();

		Unit unit = new Unit();
		unit.resolve(DatabaseImpl.class).persist(settings);
		unit.resolve(DatabaseImpl.class).persist(p);
		unit.close();
	}

	@Before
	public void before() {
		unit = new Unit();
		dbAccess = unit.resolve(DatabaseImpl.class);
		data = unit.resolve(DataFactory.class);
		aodv = unit.resolve(AodvFactory.class);
	}

	@After
	public void after() {
		unit.close();
	}

	@Test
	public void testGetAllActiveNodesInRandomOrder() {
		dbAccess.getAllActiveNodesInRandomOrder();
	}

	@Test
	public void testGetAllDataPacketsSortedByDate() {
		Players inner = new Players();
		inner.setId(1l);
		Player theNode = data.create(inner);
		dbAccess.getAllDataPacketsSortedByDate(theNode);
	}

	@Test
	public void testGetRouteRequestCount() {
		AodvDataPackets inner = new AodvDataPackets();
		inner.setPlayersBySourceId(new Players());
		inner.getPlayersBySourceId().setId(1l);
		inner.setPlayersByDestinationId(new Players());
		inner.getPlayersByDestinationId().setId(2l);
		dbAccess.getRouteRequestCount(inner);
	}

	@Test
	public void testGetSettings() {
		dbAccess.getSettings();
	}

	@Test
	public void testGetRouteRequestsByNodeAndRound() {
		Players inner = new Players();
		inner.setId(1l);
		Player theNode = data.create(inner);
		dbAccess.getRouteRequestsByNodeAndRound(theNode);
	}

	@Test
	public void testGetAODVRouteRequestBufferEntry() {
		Players inner = new Players();
		inner.setId(1l);
		AodvRoutingMessages message = new AodvRoutingMessages();
		message.setId(2l);
		message.setSequenceNumber(3l);
		Player theNode = data.create(inner);
		AodvRoutingMessage theRREQ = aodv.create(message);
		dbAccess.getAODVRouteRequestBufferEntry(theNode, theRREQ);
	}

	@Test
	public void testGetRoutingErrors() {
		Players inner = new Players();
		inner.setId(1l);
		Player p = data.create(inner);
		dbAccess.getRoutingErrors(p);
	}

	@Test
	public void testGetAllItems() {
		dbAccess.getAllItems();
	}

	@Test
	public void testGetAllIndoors() {
		dbAccess.getAllIndoors();
	}

	@Test
	public void testGetAllNodesByScoreDescending() {
		dbAccess.getAllNodesByScoreDescending();
	}

	@Test
	public void testGetPlayerById() {
		dbAccess.getPlayerById(1l);
	}

	@Test
	public void testGetAllBatteries() {
		dbAccess.getAllBatteries();
	}

	@Test
	public void testGetAllBoosters() {
		dbAccess.getAllBoosters();
	}

	@Test
	public void testCleanRoutingEntriesFor() {
		Players inner = new Players();
		inner.setId(1l);
		Player p = data.create(inner);
		dbAccess.cleanRoutingEntriesFor(p);
	}

	@Test
	public void testGetDataPacketByOwnerId() {
		Players inner = new Players();
		inner.setId(1l);
		Player p = data.create(inner);
		dbAccess.getDataPacketByOwnerId(p);
	}

	@Test
	public void testGetAllNeighboursExcept() {
		Players except = new Players();
		except.setId(2l);
		Players center = new Players();
		center.setId(1l);
		dbAccess.getAllNeighboursExcept(data.create(except), data.create(center));
	}

	@Test
	public void testGetNeighboursWithinRange() {
		Players inner = new Players();
		inner.setId(1l);
		Player center = data.create(inner);
		dbAccess.getNeighboursWithinRange(center);
	}

	@Test
	public void testRemoveRoutingEntries() {
		dbAccess.removeRoutingEntries(1l, 2l);
	}

	@Test
	public void testGetNeighbour() {
		dbAccess.getNeighbour(2l, 1l);
	}

	@Test
	public void testGetAllItemsNear() {
		Location loc = new Location();
		loc.setLatitude(52.);
		loc.setLongitude(13.);
		dbAccess.getAllItemsNear(loc, 10l);
	}

	@Test
	public void testGetPlayerByIdAndRole() {
		dbAccess.getPlayerByIdAndRole("Test", Role.NODE);
	}
}
