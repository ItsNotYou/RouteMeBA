package de.unipotsdam.nexplorer.client.android.js;

import android.app.Activity;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;

public class PlayerRadius extends UIElement {

	public Map map;
	private Circle inner;
	private LatLng latlng;
	private double range;
	private int strokeColor;
	private int strokeWeight;
	private int fillColor;

	public PlayerRadius(Activity context, int strokeColor, int strokeWeight, int fillColor) {
		super(context);
		this.strokeColor = strokeColor;
		this.strokeWeight = strokeWeight;
		this.fillColor = fillColor;
	}

	public void setCenter(final LatLng latLng) {
		this.latlng = latLng;
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				if (inner != null) {
					inner.setCenter(latLng.create());
				}
			}
		});
	}

	public void setMap(final Map map) {
		this.map = map;
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				if (map == null && inner != null) {
					inner.remove();
					inner = null;
				} else if (map != null) {
					inner = map.getMap().addCircle(new CircleOptions().center(latlng.create()).radius(range).strokeColor(strokeColor).strokeWidth(strokeWeight).fillColor(fillColor));
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
