package de.unipotsdam.nexplorer.client.android.js;

import android.app.Activity;
import android.widget.TextView;

public class Text extends UIElement {

	private TextView text;

	public Text(android.widget.TextView text, Activity host) {
		super(host);
		this.text = text;
	}

	public void html(final String string) {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				text.setText(string);
			}
		});
	}

	public void setText(final String string) {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				text.setText(string);
			}
		});
	}
}
