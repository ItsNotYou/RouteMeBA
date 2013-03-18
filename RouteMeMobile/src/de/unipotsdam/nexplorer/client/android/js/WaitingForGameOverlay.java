package de.unipotsdam.nexplorer.client.android.js;

import android.app.Dialog;

public class WaitingForGameOverlay {

	private Dialog dialog;

	public WaitingForGameOverlay(android.app.Dialog dialog) {
		this.dialog = dialog;
	}

	public void show() {
		dialog.show();
	}

	public void hide() {
		dialog.hide();
	}
}
