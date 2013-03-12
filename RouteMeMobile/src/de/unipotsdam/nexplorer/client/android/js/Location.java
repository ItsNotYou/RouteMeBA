package de.unipotsdam.nexplorer.client.android.js;

import android.app.Activity;

public class Location {

	private Activity activity;

	public Location(Activity host) {
		this.activity = host;
	}

	public void reload() {
		activity.recreate();
	}
}
