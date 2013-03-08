package de.unipotsdam.nexplorer.client.android.net.test;

import android.test.AndroidTestCase;
import de.unipotsdam.nexplorer.client.android.net.Connection;
import de.unipotsdam.nexplorer.client.android.net.GameStatus;
import de.unipotsdam.nexplorer.client.android.net.Location;
import de.unipotsdam.nexplorer.client.android.net.Status;

public class ConnectionTest extends AndroidTestCase {

	private static String domain = "routeme.dnsdynamic.com";

	private Connection sut;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		sut = new Connection(domain + ":8080");
		sut.login("Test");
	}

	public void testSendLocation() {
		Location request = new Location();
		request.setAccuracy(5.);
		request.setLatitude(13.);
		request.setLongitude(52.);

		sut.send(request);
	}

	public void testCollectItem() {
		sut.collectItem();
	}

	public void testReadGameStatus() {
		GameStatus result = sut.readGameStatus();
		assertEquals(Status.ISPAUSED, result.getStats().getGameStatus());
	}
}
