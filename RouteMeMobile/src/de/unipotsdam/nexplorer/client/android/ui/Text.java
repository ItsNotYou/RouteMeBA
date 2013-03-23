package de.unipotsdam.nexplorer.client.android.ui;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

public class Text extends UIElement {

	private TextView text;
	private Activity host;

	public Text(android.widget.TextView text, Activity host) {
		super(host);
		this.text = text;
		this.host = host;
	}

	void html(final String string, final int imageId) {
		setText(string, imageId);
	}

	private void setText(final String string, final Integer imageId) {
		if (imageId != null) {
			Drawable image = host.getResources().getDrawable(imageId);
			text.setCompoundDrawablesWithIntrinsicBounds(null, null, image, null);
		}

		text.setText(string);
		if (text.getVisibility() != View.VISIBLE) {
			text.setVisibility(View.VISIBLE);
		}
	}

	void setText(final String string) {
		setText(string, null);
	}
}
