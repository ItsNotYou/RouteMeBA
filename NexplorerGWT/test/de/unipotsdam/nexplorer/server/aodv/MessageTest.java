package de.unipotsdam.nexplorer.server.aodv;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Injector;

import de.unipotsdam.nexplorer.server.di.GuiceFactory;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.shared.DataPacket;

public class MessageTest {

	private AodvRoutingAlgorithm sut;
	private AodvFactory factory;
	private Player srcPlayer;
	private Player destPlayer;
	private Player ownerPlayer;
	private Injector injector;
	private Setting setting;

	@Before
	public void before() {
		srcPlayer = mock(Player.class);
		destPlayer = mock(Player.class);
		ownerPlayer = mock(Player.class);

		setting = mock(Setting.class);
		when(setting.getCurrentDataRound()).thenReturn(1l);

		factory = mock(AodvFactory.class);
		DatabaseImpl dbAccess = mock(DatabaseImpl.class);
		when(dbAccess.getSettings()).thenReturn(setting);

		injector = GuiceFactory.createInjector(factory, dbAccess);
		sut = injector.getInstance(AodvRoutingAlgorithm.class);
	}

	@Test
	public void testInsertNewMessage() {
		AodvNode srcNode = mock(AodvNode.class);
		when(factory.create(srcPlayer)).thenReturn(srcNode);
		Matcher<DataPacket> hasHopCount = new InitialDataPacketMatcher();

		sut.aodvInsertNewMessage(srcPlayer, destPlayer, ownerPlayer);

		verify(srcNode).enqueMessage(argThat(hasHopCount), any(List.class), any(Setting.class));
	}
}
