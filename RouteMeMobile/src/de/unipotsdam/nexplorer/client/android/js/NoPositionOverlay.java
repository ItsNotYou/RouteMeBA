package de.unipotsdam.nexplorer.client.android.js;

import android.app.Activity;
import android.app.Dialog;

public class NoPositionOverlay extends UIElement {

	private Dialog dialog;

	public NoPositionOverlay(android.app.Dialog dialog, Activity host) {
		super(host);
		this.dialog = dialog;
	}

	public void show() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				dialog.show();
			}
		});
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
