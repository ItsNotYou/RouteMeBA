package de.unipotsdam.nexplorer.client.android.net;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.location.Location;
import de.unipotsdam.nexplorer.client.android.callbacks.AjaxResult;
import de.unipotsdam.nexplorer.client.android.rest.GameStatus;
import de.unipotsdam.nexplorer.client.android.rest.LoginAnswer;
import de.unipotsdam.nexplorer.client.android.rest.Options;
import de.unipotsdam.nexplorer.client.android.rest.PingRequest;
import de.unipotsdam.nexplorer.client.android.rest.PingResponse;

public class RestMobile {

	private final String host;
	private RestTemplate template;

	public RestMobile(String host) {
		this.host = host;

		SimpleClientHttpRequestFactory http = new SimpleClientHttpRequestFactory();
		http.setConnectTimeout(8000);
		template = new RestTemplate(true, http);
		template.getMessageConverters().add(new GsonHttpMessageConverter());
	}

	public GameStatus getGameStatus(Long playerId) {
		String url = host + "/rest/mobile/get_game_status?playerId=" + playerId;
		return template.getForObject(url, GameStatus.class);
	}

	public void getGameStatus(final long playerId, final boolean isAsync, final AjaxResult<GameStatus> result) {
		ajax(new Options<GameStatus>(GameStatus.class) {

			protected void setData() {
				this.dataType = "json";
				this.url = "/rest/mobile/get_game_status";
				this.async = isAsync;
				this.data = "playerId=" + playerId;
				this.timeout = 5000;
			}

			public void success(GameStatus data) {
				result.success(data);
			}

			public void error(Exception data) {
				result.error(data);
			}
		});
	}

	public void updatePlayerPosition(final long playerId, final Location currentLocation, final AjaxResult<Object> ajaxResult) {
		ajax(new Options<Object>(Object.class) {

			@Override
			protected void setData() {
				this.type = "POST";
				this.url = "/rest/mobile/update_player_position";
				this.data = "latitude=" + currentLocation.getLatitude() + "&longitude=" + currentLocation.getLongitude() + "&accuracy=" + currentLocation.getAccuracy() + "&playerId=" + playerId + "&speed=" + currentLocation.getSpeed() + "&heading=" + currentLocation.getBearing();
				this.timeout = 5000;
			}

			public void success(Object result) {
				ajaxResult.success(result);
			}
		});
	}

	public void collectItem(final long playerId, final AjaxResult<Object> ajaxResult) {
		ajax(new Options<Object>(Object.class) {

			protected void setData() {
				this.type = "POST";
				this.url = "/rest/mobile/collect_item";
				this.data = "playerId=" + playerId;
			}

			public void success() {
				ajaxResult.success();
			}
		});
	}

	public void login(final String name, final AjaxResult<LoginAnswer> ajaxResult) {
		String url = host + "/rest/loginManager/login_player_mobile";
		String request = "name=" + name + "&isMobile=" + true;

		try {
			LoginAnswer result = template.postForObject(url, request, LoginAnswer.class);
			ajaxResult.success(result);
		} catch (Exception e) {
			ajaxResult.error(e);
		}
	}

	public LoginAnswer login(final String name) {
		String url = host + "/rest/loginManager/login_player_mobile";
		String request = "name=" + name + "&isMobile=" + true;

		return template.postForObject(url, request, LoginAnswer.class);
	}

	public void requestPing(final long playerId, final Location currentLocation, final AjaxResult<PingResponse> ajaxResult) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					final String url = host + "/rest/ping";
					final PingRequest data = new PingRequest();

					data.setNodeId(playerId);
					data.setLatitude(currentLocation.getLatitude());
					data.setLongitude(currentLocation.getLongitude());

					PingResponse result = template.postForObject(url, data, PingResponse.class);
					ajaxResult.success(result);
				} catch (Exception e) {
					ajaxResult.error(e);
				}
			}
		}).start();
	}

	private <T> void ajax(final Options<T> options) {
		Runnable job = new Runnable() {

			@Override
			public void run() {
				Object result = call(host, template, options);
				if (result instanceof Exception) {
					options.error((Exception) result);
				} else {
					options.success((T) result);
				}
			}
		};

		if (options.isAsync()) {
			new Thread(job).start();
		} else {
			job.run();
		}
	}

	private <T> Object call(String host, RestTemplate template, Options<T> options) {
		String url = host + options.getUrl();

		try {
			if (options.getType().equals("POST")) {
				return (T) template.postForObject(url, options.getData(), options.getResponseType());
			} else {
				return (T) template.getForObject(url + "?" + options.getData(), options.getResponseType());
			}
		} catch (Exception e) {
			return e;
		}
	}
}
