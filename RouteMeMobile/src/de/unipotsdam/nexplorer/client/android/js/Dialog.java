package de.unipotsdam.nexplorer.client.android.js;

import android.widget.TextView;

public class Dialog {

	private TextView text;

	public Dialog(TextView text) {
		this.text = text;
	}

	public void html(String string) {
		text.setText(string);
	}
}
