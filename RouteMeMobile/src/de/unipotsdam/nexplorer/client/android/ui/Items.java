package de.unipotsdam.nexplorer.client.android.ui;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.widget.TextView;

public class Items {

	private List<Text> items;

	public Items(Activity host, TextView... views) {
		items = new ArrayList<Text>(views.length);
		for (int count = 0; count < views.length; count++) {
			if (views[count] != null)
				items.add(new Text(views[count], host));
			else
				items.add(null);
		}
	}

	public Text[] getItems() {
		return items.toArray(new Text[0]);
	}
}
