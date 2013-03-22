package de.unipotsdam.nexplorer.client.android.js;

import android.app.Activity;

public class AppWrapper {

	private Activity activity;

	public AppWrapper(Activity host) {
		this.activity = host;
	}

	public void reload() {
		activity.recreate();
	}
}
