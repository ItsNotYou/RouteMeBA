package de.unipotsdam.nexplorer.client.android.ui;

import android.app.Activity;
import android.app.Dialog;

public class Overlay extends UIElement {

	private Dialog dialog;

	public Overlay(android.app.Dialog dialog, Activity host) {
		super(host);
		this.dialog = dialog;
	}

	void show() {
		dialog.show();
	}

	void hide() {
		dialog.hide();
	}
}
