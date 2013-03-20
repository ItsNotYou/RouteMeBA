package de.unipotsdam.nexplorer.client.android.js;

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

	public void html(final String string, final int imageId) {
		setText(string, imageId);
	}

	private void setText(final String string, final Integer imageId) {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				if (imageId != null) {
					Drawable image = host.getResources().getDrawable(imageId);
					text.setCompoundDrawablesWithIntrinsicBounds(null, null, image, null);
				}

				text.setText(string);
				if (text.getVisibility() != View.VISIBLE) {
					text.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	public void setText(final String string) {
		setText(string, null);
	}
}
