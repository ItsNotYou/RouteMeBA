package de.unipotsdam.nexplorer.client.android.js;

import android.app.Activity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import de.unipotsdam.nexplorer.client.android.sensors.MapRotator;
import de.unipotsdam.nexplorer.client.android.ui.UIElement;

public class Map extends UIElement {

	private GoogleMap map;
	private MapRotator rotator;

	public Map(GoogleMap map, Activity context, MapRotator rotator) {
		super(context);
		this.map = map;
		this.rotator = rotator;
	}

	public void setCenter(final LatLng latLng) {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				if (rotator != null) {
					rotator.setCurrentLocation(latLng.create());
				} else {
					CameraUpdate update = CameraUpdateFactory.newLatLng(latLng.create());
					map.moveCamera(update);
				}
			}
		});
	}

	public GoogleMap getMap() {
		return map;
	}
}
