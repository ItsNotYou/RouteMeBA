package de.unipotsdam.nexplorer.client.android.js;

import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import de.unipotsdam.nexplorer.client.android.R;
import de.unipotsdam.nexplorer.client.android.support.MapRotator;
import de.unipotsdam.nexplorer.client.android.ui.Button;
import de.unipotsdam.nexplorer.client.android.ui.Overlay;
import de.unipotsdam.nexplorer.client.android.ui.Text;

public class Window {

	public static Object undefined = null;

	public static AppWrapper app = null;

	public static Button loginButton = null;

	public static Text beginDialog = null;

	public static Text waitingText = null;

	public static Text hint = null;

	public static Text nextItemDistance = null;

	public static Text activeItems = null;

	public static Button collectItemButton = null;

	public static Geolocation geolocation = null;

	public static MainPanelToolbar mainPanelToolbar = null;

	public static Overlay loginOverlay = null;

	public static Overlay waitingForGameOverlay = null;
	public static Overlay noPositionOverlay = null;

	public static SenchaMap senchaMap = null;

	public static Marker playerMarker = null;
	public static PlayerRadius playerRadius = null;
	public static PlayerRadius collectionRadius = null;

	public static Activity ui = null;

	public static String host;

	public static void createInstance(android.widget.Button collectItem, android.widget.Button login, android.widget.TextView activeItemsText, android.widget.TextView hintText, android.widget.TextView nextItemDistanceText, android.widget.TextView waitingTextText, Activity host, android.widget.TextView beginText, TextView score, TextView neighbourCount, TextView remainingPlayingTime, TextView battery, android.app.Dialog loginDialog, String hostAdress, android.app.Dialog waitingForGameDialog, android.app.Dialog noPositionDialog, GoogleMap map, MapRotator rotator) {
		collectItemButton = new Button(collectItem, host);
		loginButton = new Button(login, host);

		activeItems = new Text(activeItemsText, host);
		hint = new Text(hintText, host);
		nextItemDistance = new Text(nextItemDistanceText, host);
		waitingText = new Text(waitingTextText, host);

		app = new AppWrapper(host);

		beginDialog = new Text(beginText, host);

		geolocation = new Geolocation(host, host);

		mainPanelToolbar = new MainPanelToolbar(score, neighbourCount, remainingPlayingTime, battery, host);

		loginOverlay = new Overlay(loginDialog, host);

		waitingForGameOverlay = new Overlay(waitingForGameDialog, host);
		noPositionOverlay = new Overlay(noPositionDialog, host);

		senchaMap = new SenchaMap(map, host, rotator);
		playerMarker = new Marker(host) {

			protected void setData() {
				MarkerImage image = new MarkerImage(R.drawable.home_network, new Size(16, 16),
				// The origin for this image is 0,0.
						new Point(0, 0),
						// The anchor for this image is the base of the flagpole at 0,32.
						new Point(8, 8));
				this.icon = image;
			};
		};

		int strokeColor = Color.parseColor("#5A0000FF");
		int strokeWeight = 2;
		int fillColor = Color.parseColor("#330000FF");
		playerRadius = new PlayerRadius(host, strokeColor, strokeWeight, fillColor);
		strokeColor = Color.parseColor("#5AFF0000");
		strokeWeight = 1;
		fillColor = Color.parseColor("#40FF0000");
		collectionRadius = new PlayerRadius(host, strokeColor, strokeWeight, fillColor);

		ui = host;

		Window.host = hostAdress;
	}

	public static void clearInterval(Interval interval) {
		if (interval != null) {
			interval.clear();
		}
	}

	public static Interval setInterval(TimerTask callback, long timeMillis) {
		Interval interval = new Interval();
		interval.set(callback, timeMillis);
		return interval;
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

	public static Integer parseInt(String value) {
		if (value == null) {
			return null;
		}
		return Integer.parseInt(value);
	}
}
