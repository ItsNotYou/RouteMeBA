package de.unipotsdam.nexplorer.client.android.js;

import java.util.TimerTask;

import android.app.Activity;

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

	public static void createInstance(android.widget.Button collectItem, android.widget.Button login, android.widget.TextView activeItemsText, android.widget.TextView hintText, android.widget.TextView nextItemDistanceText, android.widget.TextView waitingTextText, Activity host, android.widget.TextView beginText) {
		collectItemButton = new Button(collectItem);
		loginButton = new Button(login);

		activeItems = new Text(activeItemsText);
		hint = new Text(hintText);
		nextItemDistance = new Text(nextItemDistanceText);
		waitingText = new Text(waitingTextText);

		location = new Location(host);

		beginDialog = new Dialog(beginText);
	}

	public static void clearInterval(Interval interval) {
		interval.clear();
	}

	public static Interval setInterval(TimerTask callback, long timeMillis) {
		Interval interval = new Interval();
		interval.set(callback, timeMillis);
		return interval;
	}

	public static void ajax(Options options) {
		// TODO Port
	}

	public static <T> void each(java.util.Map<?, T> objects, Call<T> callback) {
		// TODO Port
	}

	public static boolean isNaN(double result) {
		// TODO Port
		return true;
	}

	public static double parseFloat(String value) {
		return Double.parseDouble(value);
	}
}
