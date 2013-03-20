package de.unipotsdam.nexplorer.client.android.js;

import android.app.Activity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

public class Map extends UIElement {

	private GoogleMap map;

	public Map(GoogleMap map, Activity context) {
		super(context);
		this.map = map;
	}

	public void setCenter(final LatLng latLng) {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				CameraUpdate update = CameraUpdateFactory.newLatLng(latLng.create());
				map.moveCamera(update);
			}
		});
	}

	public GoogleMap getMap() {
		return map;
	}
}
