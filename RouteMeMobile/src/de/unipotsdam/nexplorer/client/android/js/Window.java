package de.unipotsdam.nexplorer.client.android.js;

import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import de.unipotsdam.nexplorer.client.android.R;
import de.unipotsdam.nexplorer.client.android.support.MapRotator;
import de.unipotsdam.nexplorer.client.android.ui.Button;
import de.unipotsdam.nexplorer.client.android.ui.MainPanelToolbar;
import de.unipotsdam.nexplorer.client.android.ui.Overlay;
import de.unipotsdam.nexplorer.client.android.ui.Text;
import de.unipotsdam.nexplorer.client.android.ui.UI;

public class Window {

	public static Marker playerMarker = null;
	public static PlayerRadius playerRadius = null;
	public static PlayerRadius collectionRadius = null;

	public static Activity ui = null;

	public static UI createInstance(android.widget.Button collectItem, android.widget.Button login, android.widget.TextView activeItemsText, android.widget.TextView hintText, android.widget.TextView nextItemDistanceText, android.widget.TextView waitingTextText, Activity host, android.widget.TextView beginText, TextView score, TextView neighbourCount, TextView remainingPlayingTime, TextView battery, android.app.Dialog loginDialog, String hostAdress, android.app.Dialog waitingForGameDialog, android.app.Dialog noPositionDialog, GoogleMap map, MapRotator rotator) {
		Button collectItemButton = new Button(collectItem, host);
		Button loginButton = new Button(login, host);

		Text activeItems = new Text(activeItemsText, host);
		Text hint = new Text(hintText, host);
		Text nextItemDistance = new Text(nextItemDistanceText, host);
		Text waitingText = new Text(waitingTextText, host);

		Text beginDialog = new Text(beginText, host);

		MainPanelToolbar mainPanelToolbar = new MainPanelToolbar(score, neighbourCount, remainingPlayingTime, battery, host);

		Overlay loginOverlay = new Overlay(loginDialog, host);

		Overlay waitingForGameOverlay = new Overlay(waitingForGameDialog, host);
		Overlay noPositionOverlay = new Overlay(noPositionDialog, host);

		UI result = new UI(host, collectItemButton, loginButton, activeItems, hint, nextItemDistance, waitingText, beginDialog, mainPanelToolbar, loginOverlay, waitingForGameOverlay, noPositionOverlay);

		playerMarker = new Marker(host) {

			protected void setData() {
				MarkerImage image = new MarkerImage(R.drawable.home_network);
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

		return result;
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
}
