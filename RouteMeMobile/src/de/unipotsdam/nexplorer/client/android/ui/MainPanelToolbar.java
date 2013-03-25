package de.unipotsdam.nexplorer.client.android.ui;

import android.app.Activity;
import android.widget.TextView;

public class MainPanelToolbar {

	public Items items;

	public MainPanelToolbar(TextView score, TextView neighbourCount, TextView remainingPlayingTime, TextView battery, Activity host) {
		this.items = new Items(host, score, null, neighbourCount, null, remainingPlayingTime, null, battery);
	}
}
