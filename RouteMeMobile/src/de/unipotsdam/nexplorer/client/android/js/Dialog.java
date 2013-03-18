package de.unipotsdam.nexplorer.client.android.js;

import android.app.Activity;
import android.widget.TextView;

public class Dialog extends UIElement {

	private TextView text;

	public Dialog(TextView text, Activity host) {
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
}
