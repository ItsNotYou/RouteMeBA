package de.unipotsdam.nexplorer.client.android.js;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;

import de.unipotsdam.nexplorer.client.android.support.MapRotator;

public class SenchaMap {

	protected Map map;

	public SenchaMap(GoogleMap map, Activity activity, MapRotator rotator) {
		this.map = new Map(map, activity, rotator);
	}
}
