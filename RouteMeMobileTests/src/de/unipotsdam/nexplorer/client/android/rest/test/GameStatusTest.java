package de.unipotsdam.nexplorer.client.android.rest.test;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.test.AndroidTestCase;
import de.unipotsdam.nexplorer.client.android.rest.GameStatus;

public class GameStatusTest extends AndroidTestCase {

	private static final String url = "http://routeme.dnsdynamic.com:8080/rest/mobile_test/get_game_status";
	private static final int timeout = 4000;

	private GameStatus status;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		SimpleClientHttpRequestFactory http = new SimpleClientHttpRequestFactory();
		http.setConnectTimeout(timeout);
		RestTemplate template = new RestTemplate(true, http);
		template.getMessageConverters().add(new GsonHttpMessageConverter());
		this.status = template.getForObject(url, GameStatus.class);
	}

	public void testHint() {
		assertEquals("hint", status.getHint());
	}

	public void testBatterieLevel() {
		assertEquals("80.0", status.getNode().getBatterieLevel());
	}

	public void testHasRangeBooster() {
		assertEquals("1", status.getNode().getHasRangeBooster());
	}

	public void testItemInCollectionRange() {
		assertEquals(1, status.getNode().getItemInCollectionRange());
	}

	public void testNearbyItemsCount() {
		assertEquals("2", status.getNode().getNearbyItemsCount());
	}

	public void testNeighbourCount() {
		assertEquals("1", status.getNode().getNeighbourCount());
	}

	public void testNextItemDistance() {
		assertEquals("3", status.getNode().getNextItemDistance());
	}

	public void testRange() {
		assertEquals("10", status.getNode().getRange());
	}

	public void testScore() {
		assertEquals("180", status.getNode().getScore());
	}

	public void testNearbyItemsSize() {
		assertEquals(2, status.getNode().getNearbyItems().getItems().size());
	}

	public void testFirstNearbyItemType() {
		assertEquals("BATTERY", status.getNode().getNearbyItems().getItems().get(1).getItemType());
	}

	public void testFirstNearbyItemLatitude() {
		assertEquals(52.14, status.getNode().getNearbyItems().getItems().get(1).getLatitude());
	}

	public void testFirstNearbyItemLongitude() {
		assertEquals(13.77, status.getNode().getNearbyItems().getItems().get(1).getLongitude());
	}

	public void testSecondNearbyItemType() {
		assertEquals("BOOSTER", status.getNode().getNearbyItems().getItems().get(2).getItemType());
	}

	public void testSecondNearbyItemLatitude() {
		assertEquals(12.34, status.getNode().getNearbyItems().getItems().get(2).getLatitude());
	}

	public void testSecondNearbyItemLongitude() {
		assertEquals(56.78, status.getNode().getNearbyItems().getItems().get(2).getLongitude());
	}

	public void testBaseNodeRange() {
		assertEquals("9", status.getStats().getBaseNodeRange());
	}

	public void testDidEnd() {
		assertEquals("0", status.getStats().getDidEnd());
	}

	public void testGameDifficulty() {
		assertEquals("2", status.getStats().getGameDifficulty());
	}

	public void testGameExists() {
		assertEquals("1", status.getStats().getGameExists());
	}

	public void testRemainingPlayingTime() {
		assertEquals("1234567890", status.getStats().getRemainingPlayingTime());
	}
}
