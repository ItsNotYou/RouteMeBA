package de.unipotsdam.nexplorer.client.android.js;

import java.util.ArrayList;
import java.util.List;

import android.widget.TextView;

public class Items {

	private List<Text> items;

	public Items(TextView... views) {
		items = new ArrayList<Text>(views.length);
		for (int count = 0; count < views.length; count++) {
			if (views[count] != null)
				items.add(new Text(views[count]));
			else
				items.add(null);
		}
	}

	public Text[] getItems() {
		return items.toArray(new Text[0]);
	}
}
