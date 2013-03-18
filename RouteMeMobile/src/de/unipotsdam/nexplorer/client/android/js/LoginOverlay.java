package de.unipotsdam.nexplorer.client.android.js;

import android.app.Activity;
import android.app.Dialog;

public class LoginOverlay extends UIElement {

	private Dialog dialog;

	public LoginOverlay(android.app.Dialog dialog, Activity host) {
		super(host);
		this.dialog = dialog;
	}

	public void hide() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				dialog.hide();
			}
		});
	}
}
