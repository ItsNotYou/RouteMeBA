package de.unipotsdam.nexplorer.client.android.js;

import android.widget.TextView;

public class MainPanelToolbar {

	public Items items;

	public MainPanelToolbar(TextView score, TextView neighbourCount, TextView remainingPlayingTime, TextView battery) {
		this.items = new Items(score, null, neighbourCount, null, remainingPlayingTime, null, battery);
	}
}
