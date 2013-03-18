package de.unipotsdam.nexplorer.client.android.js;

import android.app.Dialog;

public class NoPositionOverlay {

	private Dialog dialog;

	public NoPositionOverlay(android.app.Dialog dialog) {
		this.dialog = dialog;
	}

	public void show() {
		dialog.show();
	}

	public void hide() {
		dialog.hide();
	}
}
