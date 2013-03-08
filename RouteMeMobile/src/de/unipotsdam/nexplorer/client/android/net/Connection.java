package de.unipotsdam.nexplorer.client.android.net;

import java.util.Locale;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class Connection {

	private final RestTemplate template;
	private final String host;
	private int id;

	public Connection(String host) {
		this.template = new RestTemplate(true);
		this.template.getMessageConverters().add(new GsonHttpMessageConverter());
		this.host = host;
	}

	public void send(Location loc) {
		String request = String.format(Locale.ENGLISH, "latitude=%s&longitude=%s&accuracy=%s&playerId=%d", loc.getLatitude(), loc.getLongitude(), loc.getAccuracy(), this.id);
		template.postForObject("http://" + host + "/rest/mobile/update_player_position", request, String.class);
	}

	public void login(String name) {
		String request = "name=" + name;
		LoginResult result = template.postForObject("http://" + host + "/rest/loginManager/login_player_mobile", request, LoginResult.class);
		this.id = result.getId();
	}

	public void collectItem() {
		String request = "playerId=" + this.id;
		template.postForObject("http://" + host + "/rest/mobile/collect_item", request, String.class);
	}

	public GameStatus readGameStatus() {
		GameStatus status = template.getForObject("http://" + host + "/rest/mobile/get_game_status?playerId=" + this.id, GameStatus.class);
		return status;
	}
}
