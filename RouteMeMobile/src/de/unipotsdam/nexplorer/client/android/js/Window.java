package de.unipotsdam.nexplorer.client.android.js;

import java.util.TimerTask;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.widget.TextView;

public class Window {

	public static Object undefined = null;

	public static Location location = null;

	public static Button loginButton = null;

	public static Dialog beginDialog = null;

	public static Text waitingText = null;

	public static Text hint = null;

	public static Text nextItemDistance = null;

	public static Text activeItems = null;

	public static Button collectItemButton = null;

	public static Geolocation geolocation = null;

	public static MainPanelToolbar mainPanelToolbar = null;

	public static LoginOverlay loginOverlay = null;

	public static WaitingForGameOverlay waitingForGameOverlay = null;
	public static NoPositionOverlay noPositionOverlay = null;

	private static RestTemplate template;
	private static String host;

	public static void createInstance(android.widget.Button collectItem, android.widget.Button login, android.widget.TextView activeItemsText, android.widget.TextView hintText, android.widget.TextView nextItemDistanceText, android.widget.TextView waitingTextText, Activity host, android.widget.TextView beginText, TextView score, TextView neighbourCount, TextView remainingPlayingTime, TextView battery, android.app.Dialog loginDialog, String hostAdress, android.app.Dialog waitingForGameDialog, android.app.Dialog noPositionDialog) {
		collectItemButton = new Button(collectItem);
		loginButton = new Button(login);

		activeItems = new Text(activeItemsText);
		hint = new Text(hintText);
		nextItemDistance = new Text(nextItemDistanceText);
		waitingText = new Text(waitingTextText);

		location = new Location(host);

		beginDialog = new Dialog(beginText);

		geolocation = new Geolocation(host);

		mainPanelToolbar = new MainPanelToolbar(score, neighbourCount, remainingPlayingTime, battery);

		loginOverlay = new LoginOverlay(loginDialog);

		waitingForGameOverlay = new WaitingForGameOverlay(waitingForGameDialog);
		noPositionOverlay = new NoPositionOverlay(noPositionDialog);

		template = new RestTemplate(true);
		template.getMessageConverters().add(new GsonHttpMessageConverter());
		Window.host = hostAdress;
	}

	public static void clearInterval(Interval interval) {
		interval.clear();
	}

	public static Interval setInterval(TimerTask callback, long timeMillis) {
		Interval interval = new Interval();
		interval.set(callback, timeMillis);
		return interval;
	}

	public static <T> void ajax(Options<T> options) {
		AjaxTask<T> task = new AjaxTask<T>(host, template, options);

		if (options.async) {
			task.execute();
		} else {
			task.doInBackground();
		}
	}

	public static <S, T> void each(java.util.Map<S, T> objects, Call<S, T> callback) {
		for (S key : objects.keySet()) {
			callback.call(key, objects.get(key));
		}
	}

	public static boolean isNaN(double result) {
		return Double.isNaN(result);
	}

	public static double parseFloat(String value) {
		return Double.parseDouble(value);
	}
}
