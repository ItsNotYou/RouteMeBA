package de.unipotsdam.nexplorer.client.android.rest.test;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.test.AndroidTestCase;
import de.unipotsdam.nexplorer.client.android.rest.LoginAnswer;

public class LoginAnswerTest extends AndroidTestCase {

	private static final String url = "http://routeme.dnsdynamic.com:8080/rest/mobile_test/login_player_mobile";
	private static final int timeout = 4000;

	private RestTemplate template;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		SimpleClientHttpRequestFactory http = new SimpleClientHttpRequestFactory();
		http.setConnectTimeout(timeout);
		this.template = new RestTemplate(true, http);
		template.getMessageConverters().add(new GsonHttpMessageConverter());
	}

	public void testIdIsReceived() {
		String request = "name=" + "IAmOk" + "&isMobile=" + true;
		LoginAnswer result = template.postForObject(url, request, LoginAnswer.class);

		assertEquals((Long) 7l, result.id);
		assertNull(result.error);
	}

	public void testNoGameCreatedError() {
		String request = "name=" + "NoGameCreated" + "&isMobile=" + true;
		LoginAnswer result = template.postForObject(url, request, LoginAnswer.class);

		assertNull(result.id);
		assertEquals((Byte) LoginAnswer.NO_GAME_CREATED, result.error);
	}

	public void testServerError() {
		String request = "name=" + "ServerError" + "&isMobile=" + true;
		LoginAnswer result = template.postForObject(url, request, LoginAnswer.class);

		assertNull(result.id);
		assertEquals((Byte) LoginAnswer.SERVER_ERROR, result.error);
	}
}
