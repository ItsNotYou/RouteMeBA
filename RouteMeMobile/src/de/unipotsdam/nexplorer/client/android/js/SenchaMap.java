package de.unipotsdam.nexplorer.client.android.js;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;

public class SenchaMap {

	protected Map map;

	public SenchaMap(GoogleMap map, Activity activity) {
		this.map = new Map(map, activity);
	}
}
