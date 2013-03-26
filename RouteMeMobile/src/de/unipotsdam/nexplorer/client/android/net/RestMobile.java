package de.unipotsdam.nexplorer.client.android.net;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import de.unipotsdam.nexplorer.client.android.callbacks.AjaxResult;
import de.unipotsdam.nexplorer.client.android.support.Location;

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

	public void getGameStatus(final int playerId, final boolean isAsync, final AjaxResult<GameStatus> result) {
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

	public void updatePlayerPosition(final int playerId, final Location currentLocation, final AjaxResult<Object> ajaxResult) {
		ajax(new Options<Object>(Object.class) {

			@Override
			protected void setData() {
				this.type = "POST";
				this.url = "/rest/mobile/update_player_position";
				this.data = "latitude=" + currentLocation.getLatitude() + "&longitude=" + currentLocation.getLongitude() + "&accuracy=" + currentLocation.getAccuracy() + "&playerId=" + playerId + "&speed=" + currentLocation.getSpeed() + "&heading=" + currentLocation.getHeading();
				this.timeout = 5000;
			}

			public void success(Object result) {
				ajaxResult.success(result);
			}
		});
	}

	public void collectItem(final int playerId, final AjaxResult<Object> ajaxResult) {
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

	public void login(final String name, final boolean isMobile, final AjaxResult<LoginAnswer> ajaxResult) {
		ajax(new Options<LoginAnswer>(LoginAnswer.class) {

			@Override
			protected void setData() {
				this.type = "POST";
				this.url = "/rest/loginManager/login_player_mobile";
				this.data = "name=" + name + "&isMobile=" + isMobile;
			}

			public void success(LoginAnswer data) {
				ajaxResult.success(data);
			}

			public void error() {
				ajaxResult.error();
			}
		});
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
