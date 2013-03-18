package de.unipotsdam.nexplorer.client.android.js;

import android.app.Activity;

public class UIElement {

	private Activity host;

	protected UIElement(Activity host) {
		this.host = host;
	}

	protected void runOnUIThread(Runnable run) {
		host.runOnUiThread(run);
	}
}
