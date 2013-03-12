package de.unipotsdam.nexplorer.client.android.js;

import android.widget.TextView;

public class Text {

	private TextView text;

	public Text(android.widget.TextView text) {
		this.text = text;
	}

	public void html(String string) {
		text.setText(string);
	}

	public void setText(String string) {
		text.setText(string);
	}
}
