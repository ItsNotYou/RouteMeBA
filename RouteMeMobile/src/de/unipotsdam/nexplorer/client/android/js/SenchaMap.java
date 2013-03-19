package de.unipotsdam.nexplorer.client.android.js;

import com.google.android.gms.maps.GoogleMap;

public class SenchaMap {

	protected Map map;

	public SenchaMap(GoogleMap map) {
		this.map = new Map(map);
	}
}
