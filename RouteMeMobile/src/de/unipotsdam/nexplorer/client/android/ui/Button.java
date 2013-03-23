package de.unipotsdam.nexplorer.client.android.ui;

import android.app.Activity;

public class Button extends UIElement {

	private final android.widget.Button button;

	public Button(android.widget.Button button, Activity host) {
		super(host);
		this.button = button;
	}

	void label(final String string) {
		button.setText(string);
	}

	boolean isDisabled() {
		return !button.isEnabled();
	}

	void enable() {
		button.setEnabled(true);
	}

	void disable() {
		button.setEnabled(false);
	}

	void html(final String string) {
		button.setText(string);
	}
}
