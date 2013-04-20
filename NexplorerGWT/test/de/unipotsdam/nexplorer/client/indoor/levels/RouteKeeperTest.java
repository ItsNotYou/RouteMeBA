package de.unipotsdam.nexplorer.client.indoor.levels;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class RouteKeeperTest {

	private RouteKeeper sut;
	private RouteListener listener;
	private Node one;
	private Node two;
	private Node three;
	private Node four;

	@Before
	public void before() {
		this.one = mock(Node.class);
		when(one.getId()).thenReturn("1");
		this.two = mock(Node.class);
		when(two.getId()).thenReturn("2");
		this.three = mock(Node.class);
		when(three.getId()).thenReturn("3");
		this.four = mock(Node.class);
		when(four.getId()).thenReturn("4");

		this.listener = mock(RouteListener.class);
		this.sut = new RouteKeeper();
		this.sut.setRouteCount(10);
		this.sut.updateAvailableNodes(Arrays.asList(one, two, three));
		this.sut.addRouteListener(listener);
	}

	@Test
	public void testCreateRoutes() {
		sut.updateAvailableNodes(Arrays.asList(two, three));
		verify(listener).updateRoutes(argThat(new HasSameContent(new Route("2", "3"), new Route("3", "2"))));
	}

	@Test
	public void testResetRoutes() {
		sut.updateAvailableNodes(Arrays.asList(two, four, one));
		verify(listener).updateRoutes(argThat(new HasSameContent(new Route("1", "2"), new Route("2", "1"), new Route("1", "4"), new Route("4", "1"), new Route("2", "4"), new Route("4", "2"))));
	}

	@Test
	public void testRemovedUsed() {
		sut.removedUsed(new Route("1", "2"));
		verify(listener).updateRoutes(argThat(new HasSameContent(new Route("2", "1"), new Route("2", "3"), new Route("3", "2"), new Route("1", "3"), new Route("3", "1"))));
	}
}
