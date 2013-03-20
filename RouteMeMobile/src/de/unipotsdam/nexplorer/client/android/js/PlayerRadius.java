package de.unipotsdam.nexplorer.client.android.js;

import android.app.Activity;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;

public class PlayerRadius extends UIElement {

	public Map map;
	private Circle inner;
	private LatLng latlng;
	private double range;

	public PlayerRadius(Activity context) {
		super(context);
	}

	public void setCenter(LatLng latLng) {
		this.latlng = latLng;
	}

	public void setMap(final Map map) {
		this.map = map;
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				if (map == null && inner != null) {
					inner.remove();
				} else if (map != null) {
					map.getMap().addCircle(new CircleOptions().center(latlng.create()).radius(range));
				}
			}
		});
	}

	public void setRadius(final double playerRange) {
		this.range = playerRange;
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				if (inner != null) {
					inner.setRadius(playerRange);
				}
			}
		});
	}
}
