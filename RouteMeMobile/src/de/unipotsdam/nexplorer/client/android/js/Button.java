package de.unipotsdam.nexplorer.client.android.js;

import android.app.Activity;

public class Button extends UIElement {

	private final android.widget.Button button;

	public Button(android.widget.Button button, Activity host) {
		super(host);
		this.button = button;
	}

	public void label(final String string) {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				button.setText(string);
			}
		});
	}

	public boolean isDisabled() {
		return !button.isEnabled();
	}

	public void enable() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				button.setEnabled(true);
			}
		});
	}

	public void disable() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				button.setEnabled(false);
			}
		});
	}

	public void html(final String string) {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				// TODO Change HTML
				button.setText(string);
			}
		});
	}
}
