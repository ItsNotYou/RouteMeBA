package de.unipotsdam.nexplorer.client.android.js;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

public class Map {

	private GoogleMap map;

	public Map(GoogleMap map) {
		this.map = map;
	}

	public void setCenter(LatLng latLng) {
		CameraUpdate update = CameraUpdateFactory.newLatLng(latLng.create());
		map.moveCamera(update);
	}

	public GoogleMap getMap() {
		return map;
	}
}
