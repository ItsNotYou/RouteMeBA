package de.unipotsdam.nexplorer.client.android.js;

import android.app.Dialog;

public class LoginOverlay {

	private Dialog dialog;

	public LoginOverlay(android.app.Dialog dialog) {
		this.dialog = dialog;
	}

	public void hide() {
		this.dialog.hide();
	}
}
