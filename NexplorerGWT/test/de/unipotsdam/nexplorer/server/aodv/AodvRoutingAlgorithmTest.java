package de.unipotsdam.nexplorer.server.aodv;

import static de.unipotsdam.nexplorer.testing.MapAssert.assertContains;
import static de.unipotsdam.nexplorer.testing.RefWalker.refEq;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.inject.Injector;

import de.unipotsdam.nexplorer.server.PojoAction;
import de.unipotsdam.nexplorer.server.data.PlayerDoesNotExistException;
import de.unipotsdam.nexplorer.server.data.Referee;
import de.unipotsdam.nexplorer.server.di.GuiceFactory;
import de.unipotsdam.nexplorer.server.persistence.DataFactory;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.Role;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvNodeData;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRouteRequestBufferEntries;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingMessages;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingTableEntries;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Neighbours;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;
import de.unipotsdam.nexplorer.shared.Aodv;

public class AodvRoutingAlgorithmTest {

	private Players srcPlayer;
	private Players destPlayer;
	private Players ownerPlayer;
	private Players otherPlayer;
	private Settings settings;
	private DatabaseImpl dbAccess;
	private Locator locator;
	private Referee referee;
	private Player src;
	private Player dest;
	private Player owner;
	private Player other;
	private Injector injector;
	private AodvFactory factory;
	private DataFactory data;

	@Before
	public void setUp() {
		srcPlayer = new Players();
		srcPlayer.setAodvDataPacketsesForCurrentNodeId(new HashSet<AodvDataPackets>());
		srcPlayer.setAodvDataPacketsesForDestinationId(new HashSet<AodvDataPackets>());
		srcPlayer.setAodvDataPacketsesForOwnerId(new HashSet<AodvDataPackets>());
		srcPlayer.setAodvDataPacketsesForSourceId(new HashSet<AodvDataPackets>());
		srcPlayer.setAodvNodeDatas(new HashSet<AodvNodeData>());
		srcPlayer.setBattery(50.);
		srcPlayer.setHasSignalRangeBooster(0l);
		srcPlayer.setHasSignalStrengthBooster(0l);
		srcPlayer.setId(1l);
		srcPlayer.setLastPositionUpdate(new Date().getTime());
		srcPlayer.setLatitude(0.);
		srcPlayer.setLongitude(0.);
		srcPlayer.setName("Source");
		srcPlayer.setNeighbourses(new HashSet<Neighbours>());
		srcPlayer.setRemainingHighPriorityMessages(0);
		srcPlayer.setRole(Role.NODE);
		srcPlayer.setScore(0l);
		srcPlayer.setSequenceNumber(5l);

		destPlayer = new Players();
		destPlayer.setAodvDataPacketsesForCurrentNodeId(new HashSet<AodvDataPackets>());
		destPlayer.setAodvDataPacketsesForDestinationId(new HashSet<AodvDataPackets>());
		destPlayer.setAodvDataPacketsesForOwnerId(new HashSet<AodvDataPackets>());
		destPlayer.setAodvDataPacketsesForSourceId(new HashSet<AodvDataPackets>());
		destPlayer.setAodvNodeDatas(new HashSet<AodvNodeData>());
		destPlayer.setBattery(75.);
		destPlayer.setHasSignalRangeBooster(0l);
		destPlayer.setHasSignalStrengthBooster(0l);
		destPlayer.setId(2l);
		destPlayer.setLastPositionUpdate(new Date().getTime());
		destPlayer.setLatitude(0.);
		destPlayer.setLongitude(0.);
		destPlayer.setName("Destination");
		destPlayer.setNeighbourses(new HashSet<Neighbours>());
		destPlayer.setRemainingHighPriorityMessages(0);
		destPlayer.setRole(Role.NODE);
		destPlayer.setScore(10l);
		destPlayer.setSequenceNumber(55l);

		ownerPlayer = new Players();
		ownerPlayer.setAodvDataPacketsesForCurrentNodeId(new HashSet<AodvDataPackets>());
		ownerPlayer.setAodvDataPacketsesForDestinationId(new HashSet<AodvDataPackets>());
		ownerPlayer.setAodvDataPacketsesForOwnerId(new HashSet<AodvDataPackets>());
		ownerPlayer.setAodvDataPacketsesForSourceId(new HashSet<AodvDataPackets>());
		ownerPlayer.setAodvNodeDatas(new HashSet<AodvNodeData>());
		ownerPlayer.setBattery(0.);
		ownerPlayer.setHasSignalRangeBooster(0l);
		ownerPlayer.setHasSignalStrengthBooster(0l);
		ownerPlayer.setId(3l);
		ownerPlayer.setLastPositionUpdate(0l);
		ownerPlayer.setLatitude(0.);
		ownerPlayer.setLongitude(0.);
		ownerPlayer.setName("Owner");
		ownerPlayer.setNeighbourses(new HashSet<Neighbours>());
		ownerPlayer.setRemainingHighPriorityMessages(0);
		ownerPlayer.setRole(Role.MESSAGE);
		ownerPlayer.setScore(100l);
		ownerPlayer.setSequenceNumber(0l);

		otherPlayer = new Players();
		otherPlayer.setAodvDataPacketsesForCurrentNodeId(new HashSet<AodvDataPackets>());
		otherPlayer.setAodvDataPacketsesForDestinationId(new HashSet<AodvDataPackets>());
		otherPlayer.setAodvDataPacketsesForOwnerId(new HashSet<AodvDataPackets>());
		otherPlayer.setAodvDataPacketsesForSourceId(new HashSet<AodvDataPackets>());
		otherPlayer.setAodvNodeDatas(new HashSet<AodvNodeData>());
		otherPlayer.setBattery(100.);
		otherPlayer.setHasSignalRangeBooster(0l);
		otherPlayer.setHasSignalStrengthBooster(0l);
		otherPlayer.setId(4l);
		otherPlayer.setLastPositionUpdate(new Date().getTime());
		otherPlayer.setLatitude(0.);
		otherPlayer.setLongitude(0.);
		otherPlayer.setName("Other");
		otherPlayer.setNeighbourses(new HashSet<Neighbours>());
		otherPlayer.setRemainingHighPriorityMessages(0);
		otherPlayer.setRole(Role.NODE);
		otherPlayer.setScore(1000l);
		otherPlayer.setSequenceNumber(555l);

		settings = new Settings();
		settings.setBaseNodeRange(10l);
		settings.setBonusGoal(1l);
		settings.setCurrentDataPacketProcessingRound(2l);
		settings.setCurrentRoutingMessageProcessingRound(22l);
		settings.setDidEnd((byte) 0);
		settings.setDifficulty(0l);
		settings.setId(0l);
		settings.setIsRunning((byte) 1);
		settings.setItemCollectionRange(5l);
		settings.setLastPause(0l);
		settings.setMaxBatteries(10l);
		settings.setMaxBoosters(10l);
		settings.setPlayingFieldLowerRightLatitude(75.);
		settings.setPlayingFieldLowerRightLongitude(75.);
		settings.setPlayingFieldUpperLeftLatitude(0.);
		settings.setPlayingFieldUpperLeftLongitude(0.);
		settings.setPlayingTime(180l);
		settings.setProtocol("aodv");
		settings.setRemainingPlayingTime(170l);
		settings.setRunningSince(new Date().getTime() - 10);
		settings.setUpdateDisplayIntervalTime(3000l);
		settings.setUpdatePositionIntervalTime(3000l);

		dbAccess = mock(DatabaseImpl.class);
		when(dbAccess.getSettings()).thenReturn(new Setting(settings, dbAccess));

		locator = mock(Locator.class);
		referee = mock(Referee.class);
		injector = GuiceFactory.createInjector(dbAccess, locator, referee);
		data = injector.getInstance(DataFactory.class);

		src = data.create(srcPlayer);
		dest = data.create(destPlayer);
		owner = data.create(ownerPlayer);
		other = data.create(otherPlayer);

		when(dbAccess.getPlayerById(1l)).thenReturn(src);
		when(dbAccess.getPlayerById(2l)).thenReturn(dest);
		when(dbAccess.getPlayerById(3l)).thenReturn(owner);
		when(dbAccess.getPlayerById(4l)).thenReturn(other);
		when(dbAccess.getAllPlayers()).thenReturn(Arrays.asList(src, dest, owner, other));

		factory = injector.getInstance(AodvFactory.class);
	}

	private void makeNeighbours(Player one, Player two, Players pOne, Players pTwo) {
		when(locator.isInRange(refEq(one), refEq(two))).thenReturn(true);

		pOne.getNeighbourses().add(new Neighbours(pTwo, pOne));
		pTwo.getNeighbourses().add(new Neighbours(pOne, pTwo));
	}

	private void makeExNeighbours(Player one, Player two, Players pOne, Players pTwo) {
		when(locator.isInRange(refEq(one), refEq(two))).thenReturn(false);

		pOne.getNeighbourses().add(new Neighbours(pTwo, pOne));
		pTwo.getNeighbourses().add(new Neighbours(pOne, pTwo));
	}

	@Test
	public void testInsertNewMessageWithoutRoute() throws PlayerDoesNotExistException {
		long dataProcessingRound = 5l;
		long routingProcessingRound = 15l;
		settings.setCurrentDataPacketProcessingRound(dataProcessingRound);
		settings.setCurrentRoutingMessageProcessingRound(routingProcessingRound);

		long srcSequenceNumber = 1337l;
		srcPlayer.setSequenceNumber(srcSequenceNumber);

		makeNeighbours(src, other, srcPlayer, otherPlayer);

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		Map<Object, PojoAction> result = sut.aodvInsertNewMessage(src, dest, owner, new LinkedList<AodvRoutingTableEntries>(), new Setting(settings, dbAccess));

		AodvRoutingMessages RREQ = new AodvRoutingMessages(other.getId(), Aodv.ROUTING_MESSAGE_TYPE_RREQ, src.getId(), dest.getId(), 8l, srcSequenceNumber + 1, 1l, "1", routingProcessingRound + 1);
		AodvDataPackets dataPacket = new AodvDataPackets(destPlayer, ownerPlayer, srcPlayer, srcPlayer, (short) 0, Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE, dataProcessingRound + 1, (byte) 0);
		AodvRouteRequestBufferEntries bufferEntry = new AodvRouteRequestBufferEntries(src.getId(), src.getId(), srcPlayer.getSequenceNumber());

		verify(dbAccess).persist(srcPlayer);
		assertContains(RREQ, PojoAction.SAVE, result);
		assertTrue(result.keySet().contains(dataPacket));
		assertTrue(result.keySet().contains(bufferEntry));
	}

	@Test
	public void testInsertNewMessageWithInsufficientNodeBattery() throws PlayerDoesNotExistException {
		srcPlayer.setBattery(0.);
		makeNeighbours(src, other, srcPlayer, otherPlayer);

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		Map<Object, PojoAction> result = sut.aodvInsertNewMessage(src, dest, owner, new LinkedList<AodvRoutingTableEntries>(), new Setting(settings, dbAccess));

		assertTrue(result.isEmpty());
	}

	@Test
	public void testInsertNewMessageWithRoute() throws PlayerDoesNotExistException {
		AodvRoutingTableEntries srcRoute = new AodvRoutingTableEntries();
		srcRoute.setDestinationId(destPlayer.getId());
		srcRoute.setDestinationSequenceNumber(destPlayer.getSequenceNumber());
		srcRoute.setHopCount(2l);
		srcRoute.setNextHopId(otherPlayer.getId());
		srcRoute.setNodeId(srcPlayer.getId());
		srcRoute.setTimestamp(new Date().getTime());

		AodvRoutingTableEntries otherRoute = new AodvRoutingTableEntries();
		otherRoute.setDestinationId(destPlayer.getId());
		otherRoute.setDestinationSequenceNumber(destPlayer.getSequenceNumber());
		otherRoute.setHopCount(1l);
		otherRoute.setNextHopId(destPlayer.getId());
		otherRoute.setNodeId(otherPlayer.getId());
		otherRoute.setTimestamp(new Date().getTime());

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		Map<Object, PojoAction> result = sut.aodvInsertNewMessage(src, dest, owner, Arrays.asList(srcRoute, otherRoute), new Setting(settings, dbAccess));

		AodvDataPackets packet = new AodvDataPackets();
		packet.setStatus(Aodv.DATA_PACKET_STATUS_UNDERWAY);
		packet.setDidReachBonusGoal((byte) 0);
		packet.setHopsDone((short) 0);
		packet.setPlayersByCurrentNodeId(srcPlayer);
		packet.setPlayersByDestinationId(destPlayer);
		packet.setPlayersByOwnerId(ownerPlayer);
		packet.setPlayersBySourceId(srcPlayer);
		packet.setProcessingRound(3l);

		assertTrue(result.keySet().contains(packet));
	}

	@Test
	public void testProcessDataPacketsWithPacketWaitingForRoute() {
		List<Player> allActiveNodesInRandomOrder = Arrays.asList(src);

		settings.setCurrentDataPacketProcessingRound(5l);
		settings.setCurrentRoutingMessageProcessingRound(1l);

		AodvDataPackets packets = new AodvDataPackets();
		packets.setStatus(Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE);
		packets.setDidReachBonusGoal(null);
		packets.setHopsDone(null);
		packets.setId(2l);
		packets.setPlayersByCurrentNodeId(srcPlayer);
		packets.setPlayersByDestinationId(destPlayer);
		packets.setPlayersByOwnerId(ownerPlayer);
		packets.setPlayersBySourceId(srcPlayer);
		packets.setProcessingRound(5l);

		srcPlayer.setAodvDataPacketsesForCurrentNodeId(Sets.newHashSet(packets));

		AodvRoutingMessages message = new AodvRoutingMessages();
		message.setType(Aodv.ROUTING_MESSAGE_TYPE_RREQ);
		message.setSourceId(srcPlayer.getId());
		message.setDestinationId(destPlayer.getId());
		when(dbAccess.getAllRoutingMessages()).thenReturn(Arrays.asList(message));

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		Map<Object, PojoAction> actual = sut.aodvProcessDataPackets(allActiveNodesInRandomOrder, new LinkedList<AodvRoutingTableEntries>(), new Setting(settings, dbAccess));

		AodvDataPackets result = new AodvDataPackets();
		result.setStatus(Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE);
		result.setDidReachBonusGoal(null);
		result.setHopsDone(null);
		result.setId(2l);
		result.setPlayersByCurrentNodeId(srcPlayer);
		result.setPlayersByDestinationId(destPlayer);
		result.setPlayersByOwnerId(ownerPlayer);
		result.setPlayersBySourceId(srcPlayer);
		result.setProcessingRound(6l);

		assertContains(result, PojoAction.SAVE, actual);
	}

	@Test
	public void testProcessDataPacketWithNewRoute() {
		List<Player> allActiveNodesInRandomOrder = Arrays.asList(src);
		makeNeighbours(src, other, srcPlayer, otherPlayer);

		settings.setCurrentDataPacketProcessingRound(5l);
		settings.setCurrentRoutingMessageProcessingRound(1l);

		AodvDataPackets packets = new AodvDataPackets();
		packets.setStatus(Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE);
		packets.setDidReachBonusGoal(null);
		packets.setHopsDone((short) 0);
		packets.setId(2l);
		packets.setPlayersByCurrentNodeId(srcPlayer);
		packets.setPlayersByDestinationId(destPlayer);
		packets.setPlayersByOwnerId(ownerPlayer);
		packets.setPlayersBySourceId(srcPlayer);
		packets.setProcessingRound(5l);

		AodvRoutingTableEntries routings = new AodvRoutingTableEntries();
		routings.setDestinationId(destPlayer.getId());
		routings.setDestinationSequenceNumber(destPlayer.getSequenceNumber());
		routings.setHopCount(2l);
		routings.setId(1l);
		routings.setNextHopId(otherPlayer.getId());
		routings.setNodeId(srcPlayer.getId());
		routings.setTimestamp(new Date().getTime());

		AodvRoutingMessages message = new AodvRoutingMessages();
		message.setType(Aodv.ROUTING_MESSAGE_TYPE_RREQ);
		message.setSourceId(srcPlayer.getId());
		message.setDestinationId(destPlayer.getId());

		srcPlayer.setAodvDataPacketsesForCurrentNodeId(Sets.newHashSet(packets));
		when(dbAccess.getAllRoutingMessages()).thenReturn(Arrays.asList(message));
		List<AodvRoutingTableEntries> allRoutingTableEntries = Arrays.asList(routings);

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		Map<Object, PojoAction> actual = sut.aodvProcessDataPackets(allActiveNodesInRandomOrder, allRoutingTableEntries, new Setting(settings, dbAccess));

		AodvDataPackets result = new AodvDataPackets();
		result.setStatus(Aodv.DATA_PACKET_STATUS_UNDERWAY);
		result.setDidReachBonusGoal(null);
		result.setHopsDone((short) 1);
		result.setId(null);
		result.setPlayersByCurrentNodeId(otherPlayer);
		result.setPlayersByDestinationId(destPlayer);
		result.setPlayersByOwnerId(ownerPlayer);
		result.setPlayersBySourceId(srcPlayer);
		result.setProcessingRound(6l);

		assertContains(result, PojoAction.SAVE, actual);
		assertContains(packets, PojoAction.DELETE, actual);
	}

	@Test
	public void testProcessDataPacketWithKnownRoute() {
		List<Player> allActiveNodesInRandomOrder = Arrays.asList(src);
		makeNeighbours(src, other, srcPlayer, otherPlayer);

		settings.setCurrentDataPacketProcessingRound(3l);

		AodvDataPackets packets = new AodvDataPackets();
		packets.setDidReachBonusGoal(null);
		packets.setHopsDone((short) 0);
		packets.setId(2l);
		packets.setPlayersByCurrentNodeId(srcPlayer);
		packets.setPlayersByDestinationId(destPlayer);
		packets.setPlayersByOwnerId(ownerPlayer);
		packets.setPlayersBySourceId(srcPlayer);
		packets.setProcessingRound(3l);
		packets.setStatus(Aodv.DATA_PACKET_STATUS_UNDERWAY);
		srcPlayer.setAodvDataPacketsesForCurrentNodeId(Sets.newHashSet(packets));

		AodvRoutingTableEntries fromSrcToOther = new AodvRoutingTableEntries();
		fromSrcToOther.setDestinationId(destPlayer.getId());
		fromSrcToOther.setDestinationSequenceNumber(2l);
		fromSrcToOther.setHopCount(2l);
		fromSrcToOther.setId(1l);
		fromSrcToOther.setNextHopId(otherPlayer.getId());
		fromSrcToOther.setNodeId(srcPlayer.getId());
		fromSrcToOther.setTimestamp(new Date().getTime());

		AodvRoutingTableEntries fromOtherToDest = new AodvRoutingTableEntries();
		fromOtherToDest.setDestinationId(destPlayer.getId());
		fromOtherToDest.setDestinationSequenceNumber(2l);
		fromOtherToDest.setHopCount(1l);
		fromOtherToDest.setId(2l);
		fromOtherToDest.setNextHopId(destPlayer.getId());
		fromOtherToDest.setNodeId(otherPlayer.getId());
		fromOtherToDest.setTimestamp(new Date().getTime());
		List<AodvRoutingTableEntries> allRoutingTableEntries = Arrays.asList(fromSrcToOther, fromOtherToDest);

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		Map<Object, PojoAction> actual = sut.aodvProcessDataPackets(allActiveNodesInRandomOrder, allRoutingTableEntries, new Setting(settings, dbAccess));

		AodvDataPackets result = new AodvDataPackets();
		result.setDidReachBonusGoal(null);
		result.setHopsDone((short) 1);
		result.setId(null);
		result.setPlayersByCurrentNodeId(otherPlayer);
		result.setPlayersByDestinationId(destPlayer);
		result.setPlayersByOwnerId(ownerPlayer);
		result.setPlayersBySourceId(srcPlayer);
		result.setProcessingRound(4l);
		result.setStatus(Aodv.DATA_PACKET_STATUS_UNDERWAY);

		assertContains(result, PojoAction.SAVE, actual);
		assertContains(packets, PojoAction.DELETE, actual);
	}

	@Test
	public void testProcessRREQTransmission() {
		long currentRoutingRound = 322;
		settings.setCurrentRoutingMessageProcessingRound(currentRoutingRound);

		makeNeighbours(src, other, srcPlayer, otherPlayer);
		when(dbAccess.getAllActiveNodesInRandomOrder()).thenReturn(Arrays.asList(src));

		AodvRoutingTableEntries entry = new AodvRoutingTableEntries();
		entry.setDestinationId(other.getId());
		entry.setDestinationSequenceNumber(otherPlayer.getSequenceNumber());
		entry.setHopCount(1l);
		entry.setId(1l);
		entry.setNextHopId(other.getId());
		entry.setNodeId(src.getId());
		entry.setTimestamp(new Date().getTime());
		when(dbAccess.getAllRoutingTableEntries()).thenReturn(Arrays.asList(entry));

		AodvRoutingMessages rreq = new AodvRoutingMessages();
		rreq.setCurrentNodeId(src.getId());
		rreq.setDestinationId(dest.getId());
		rreq.setHopCount(0l);
		rreq.setId(2l);
		rreq.setLifespan(9l);
		rreq.setPassedNodes(null);
		rreq.setProcessingRound(currentRoutingRound);
		rreq.setSequenceNumber(12l);
		rreq.setSourceId(src.getId());
		rreq.setType(Aodv.ROUTING_MESSAGE_TYPE_RREQ);
		when(dbAccess.getRouteRequestsByRound()).thenReturn(Arrays.asList(factory.create(rreq)));

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		Map<Object, PojoAction> actual = sut.aodvProcessRoutingMessages(new Setting(settings, dbAccess));

		AodvRoutingMessages result = new AodvRoutingMessages();
		result.setCurrentNodeId(other.getId());
		result.setDestinationId(dest.getId());
		result.setHopCount(1l);
		result.setId(null);
		result.setLifespan(8l);
		result.setPassedNodes(src.getId().toString());
		result.setProcessingRound(currentRoutingRound + 1);
		result.setSequenceNumber(12l);
		result.setSourceId(src.getId());
		result.setType(Aodv.ROUTING_MESSAGE_TYPE_RREQ);

		assertContains(result, PojoAction.SAVE, actual);
		assertContains(rreq, PojoAction.DELETE, actual);
	}

	@Test
	public void testProcessRREQToDestination() {
		long currentRoutingRound = 322;
		settings.setCurrentRoutingMessageProcessingRound(currentRoutingRound);

		makeNeighbours(src, dest, srcPlayer, destPlayer);
		when(dbAccess.getAllActiveNodesInRandomOrder()).thenReturn(Arrays.asList(dest));

		AodvRoutingTableEntries entry = new AodvRoutingTableEntries();
		entry.setDestinationId(src.getId());
		entry.setDestinationSequenceNumber(srcPlayer.getSequenceNumber());
		entry.setHopCount(1l);
		entry.setId(1l);
		entry.setNextHopId(src.getId());
		entry.setNodeId(dest.getId());
		entry.setTimestamp(new Date().getTime());
		when(dbAccess.getAllRoutingTableEntries()).thenReturn(Arrays.asList(entry));

		AodvRoutingMessages rreq = new AodvRoutingMessages();
		rreq.setCurrentNodeId(dest.getId());
		rreq.setDestinationId(dest.getId());
		rreq.setHopCount(1l);
		rreq.setId(2l);
		rreq.setLifespan(8l);
		rreq.setPassedNodes("1");
		rreq.setProcessingRound(currentRoutingRound);
		rreq.setSequenceNumber(12l);
		rreq.setSourceId(src.getId());
		rreq.setType(Aodv.ROUTING_MESSAGE_TYPE_RREQ);
		when(dbAccess.getRouteRequestsByRound()).thenReturn(Arrays.asList(factory.create(rreq)));

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		Map<Object, PojoAction> result = sut.aodvProcessRoutingMessages(new Setting(settings, dbAccess));

		AodvRoutingTableEntries expected = new AodvRoutingTableEntries();
		expected.setDestinationId(dest.getId());
		expected.setDestinationSequenceNumber(12l);
		expected.setHopCount(1l);
		expected.setId(null);
		expected.setNextHopId(dest.getId());
		expected.setNodeId(src.getId());

		assertContains(expected, new String[] { "timestamp" }, PojoAction.SAVE, result);
	}

	@Test
	public void testTransmitionOfRERR() {
		long currentRoutingRound = 322;
		settings.setCurrentRoutingMessageProcessingRound(currentRoutingRound);

		makeNeighbours(src, other, srcPlayer, otherPlayer);
		makeNeighbours(other, dest, otherPlayer, destPlayer);
		when(dbAccess.getAllActiveNodesInRandomOrder()).thenReturn(Arrays.asList(src));

		AodvRoutingTableEntries entry = new AodvRoutingTableEntries();
		entry.setDestinationId(destPlayer.getId());
		entry.setDestinationSequenceNumber(destPlayer.getSequenceNumber());
		entry.setHopCount(2l);
		entry.setId(1l);
		entry.setNextHopId(otherPlayer.getId());
		entry.setNodeId(srcPlayer.getId());
		entry.setTimestamp(new Date().getTime());
		when(dbAccess.getAllRoutingTableEntries()).thenReturn(Arrays.asList(entry));

		AodvRoutingMessages rerr = new AodvRoutingMessages();
		rerr.setCurrentNodeId(src.getId());
		rerr.setDestinationId(dest.getId());
		rerr.setHopCount(0l);
		rerr.setId(1l);
		rerr.setLifespan(9l);
		rerr.setPassedNodes(null);
		rerr.setProcessingRound(0l);
		rerr.setSequenceNumber(3l);
		rerr.setSourceId(src.getId());
		rerr.setType(Aodv.ROUTING_MESSAGE_TYPE_RERR);
		when(dbAccess.getRoutingErrors()).thenReturn(Arrays.asList(factory.create(rerr)));

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		Map<Object, PojoAction> actual = sut.aodvProcessRoutingMessages(new Setting(settings, dbAccess));

		AodvRoutingMessages result = new AodvRoutingMessages();
		result.setCurrentNodeId(other.getId());
		result.setDestinationId(dest.getId());
		result.setHopCount(1l);
		result.setId(null);
		result.setLifespan(8l);
		result.setPassedNodes(Long.toString(src.getId()));
		result.setProcessingRound(currentRoutingRound + 1);
		result.setSequenceNumber(3l);
		result.setSourceId(src.getId());
		result.setType(Aodv.ROUTING_MESSAGE_TYPE_RERR);

		assertContains(result, PojoAction.SAVE, actual);
		assertContains(rerr, PojoAction.DELETE, actual);
	}

	@Test
	public void testCreationOfRERR() {

	}

	@Test
	public void testNotifyOfLostNeighbours() {
		long currentRoutingRound = 322;
		settings.setCurrentRoutingMessageProcessingRound(currentRoutingRound);
		long srcSequenceNumber = 144;
		srcPlayer.setSequenceNumber(srcSequenceNumber);

		makeExNeighbours(src, other, srcPlayer, otherPlayer);
		makeNeighbours(src, dest, srcPlayer, destPlayer);
		when(dbAccess.getNeighboursWithinRange(src)).thenReturn(Arrays.asList(dest));

		AodvRoutingTableEntries fromSrcToDest = new AodvRoutingTableEntries();
		fromSrcToDest.setDestinationId(destPlayer.getId());
		fromSrcToDest.setDestinationSequenceNumber(destPlayer.getSequenceNumber());
		fromSrcToDest.setHopCount(1l);
		fromSrcToDest.setId(1l);
		fromSrcToDest.setNextHopId(destPlayer.getId());
		fromSrcToDest.setNodeId(srcPlayer.getId());
		fromSrcToDest.setTimestamp(new Date().getTime());

		Neighbours neighbour = new Neighbours();
		neighbour.setId(1l);
		neighbour.setNode(srcPlayer);
		neighbour.setNeighbour(destPlayer);

		Neighbours otherNeighbour = new Neighbours();
		otherNeighbour.setId(2l);
		otherNeighbour.setNode(srcPlayer);
		otherNeighbour.setNeighbour(otherPlayer);

		when(dbAccess.getNeighbour(dest.getId(), src.getId())).thenReturn(data.create(neighbour));

		AodvNode sut = injector.getInstance(AodvFactory.class).create(src);
		Map<Object, PojoAction> actual = sut.updateNeighbourhood(Arrays.asList(data.create(neighbour), data.create(otherNeighbour)), currentRoutingRound, Arrays.asList(fromSrcToDest), new Setting(settings, dbAccess));

		AodvRoutingMessages rerr = new AodvRoutingMessages();
		rerr.setCurrentNodeId(dest.getId());
		rerr.setDestinationId(other.getId());
		rerr.setHopCount(null);
		rerr.setId(null);
		rerr.setLifespan(null);
		rerr.setPassedNodes(src.getId().toString());
		rerr.setProcessingRound(currentRoutingRound + 1);
		rerr.setSequenceNumber(srcSequenceNumber + 1);
		rerr.setSourceId(src.getId());
		rerr.setType(Aodv.ROUTING_MESSAGE_TYPE_RERR);

		assertContains(rerr, PojoAction.SAVE, actual);
	}

	@Test
	public void testNotifyOfPacketArrival() {
		long currentDataRound = 23;
		settings.setCurrentDataPacketProcessingRound(currentDataRound);

		makeNeighbours(src, dest, srcPlayer, destPlayer);
		List<Player> allActiveNodesInRandomOrder = Arrays.asList(src);

		AodvRoutingTableEntries entry = new AodvRoutingTableEntries();
		entry.setDestinationId(destPlayer.getId());
		entry.setDestinationSequenceNumber(destPlayer.getSequenceNumber());
		entry.setHopCount(1l);
		entry.setId(1l);
		entry.setNextHopId(destPlayer.getId());
		entry.setNodeId(srcPlayer.getId());
		entry.setTimestamp(new Date().getTime());
		List<AodvRoutingTableEntries> allRoutingTableEntries = Arrays.asList(entry);

		AodvDataPackets packet = new AodvDataPackets();
		packet.setDidReachBonusGoal(null);
		packet.setHopsDone((short) 0);
		packet.setId(1l);
		packet.setPlayersByCurrentNodeId(srcPlayer);
		packet.setPlayersByDestinationId(destPlayer);
		packet.setPlayersByOwnerId(ownerPlayer);
		packet.setPlayersBySourceId(srcPlayer);
		packet.setProcessingRound(currentDataRound);
		packet.setStatus(Aodv.DATA_PACKET_STATUS_UNDERWAY);
		srcPlayer.setAodvDataPacketsesForCurrentNodeId(Sets.newHashSet(packet));

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		Map<Object, PojoAction> actual = sut.aodvProcessDataPackets(allActiveNodesInRandomOrder, allRoutingTableEntries, new Setting(settings, dbAccess));

		AodvDataPackets result = new AodvDataPackets();
		result.setDidReachBonusGoal(null);
		result.setHopsDone((short) 1);
		result.setId(null);
		result.setPlayersByCurrentNodeId(destPlayer);
		result.setPlayersByDestinationId(destPlayer);
		result.setPlayersByOwnerId(ownerPlayer);
		result.setPlayersBySourceId(srcPlayer);
		result.setProcessingRound(currentDataRound + 1);
		result.setStatus(Aodv.DATA_PACKET_STATUS_ARRIVED);

		verify(referee).packetArrived(any(Setting.class), any(AodvDataPacket.class));

		assertContains(result, PojoAction.SAVE, actual);
	}
}
