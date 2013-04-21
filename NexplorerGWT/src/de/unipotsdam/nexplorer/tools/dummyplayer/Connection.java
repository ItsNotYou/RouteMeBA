package de.unipotsdam.nexplorer.tools.dummyplayer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class Connection {

	private static Client client = null;

	private static Client getClient() {
		if (client == null) {
			createClientOnce();
		}
		return client;
	}

	private static synchronized void createClientOnce() {
		if (client == null) {
			client = Client.create();
		}
	}

	private String host;
	private int id;

	public Connection(String host) {
		this.host = host;
	}

	public void sendLocation(Location loc) {
		try {
			Client client = getClient();
			Stopwatch watch = Stopwatch.start("sendLocation");

			WebResource res = client.resource("http://" + host + "/rest/mobile/update_player_position");
			res.post(String.format("latitude=%s&longitude=%s&accuracy=%s&playerId=%d", loc.getLatitude(), loc.getLongitude(), loc.getAccuracy(), this.id));

			watch.stop();
		} catch (Exception e) {
		}
	}

	public void login(String name) {
		Client client = getClient();
		Stopwatch watch = Stopwatch.start("login");

		WebResource res = client.resource("http://" + host + "/rest/loginManager/login_player_mobile");
		LoginResult result = res.post(LoginResult.class, "name=" + name);
		this.id = result.id;

		watch.stop();
	}

	public Settings readGameStatus() {
		Client client = getClient();
		Stopwatch watch = Stopwatch.start("readGameStatus");

		WebResource res = client.resource("http://" + host + "/rest/mobile/get_game_status?playerId=" + this.id);
		Settings result = res.get(Settings.class);

		watch.stop();
		return result;
	}

	public void collectItem() {
		try {
			Client client = getClient();
			Stopwatch watch = Stopwatch.start("collectItem");

			WebResource res = client.resource("http://" + host + "/rest/mobile/collect_item");
			res.post("playerId=" + this.id);

			watch.stop();
		} catch (Exception e) {
		}
	}

	public void ping() {
		try {
			Client client = getClient();
			Stopwatch watch = Stopwatch.start("ping");

			WebResource res = client.resource("http://" + host + "/rest/ping/");
			res.post(new Ping(this.id));

			watch.stop();
		} catch (Exception e) {
		}
	}
}
