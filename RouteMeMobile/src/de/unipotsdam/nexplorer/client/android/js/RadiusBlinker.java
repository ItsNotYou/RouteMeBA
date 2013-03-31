package de.unipotsdam.nexplorer.client.android.js;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;

import de.unipotsdam.nexplorer.client.android.callbacks.Locatable;
import de.unipotsdam.nexplorer.client.android.callbacks.Pingable;
import de.unipotsdam.nexplorer.client.android.support.Location;

public class RadiusBlinker implements Locatable, Pingable {

	private static final double maxSize = 15;
	private static final int frequency = 50;
	private static final double maxTime = 500;

	private final GoogleMap map;
	private final Activity host;
	private final double sizeDelta;
	private Location location;

	public RadiusBlinker(GoogleMap map, Activity host) {
		this.map = map;
		this.host = host;

		this.sizeDelta = maxSize / (maxTime / frequency);
	}

	@Override
	public void locationChanged(Location location) {
		this.location = location;
	}

	@Override
	public void pingRequested() {
		Location loc = location;
		if (loc != null) {
			new Blink(new LatLng(loc));
		}
	}

	private class Blink extends TimerTask {

		private final Timer timer;
		private Circle circle;

		public Blink(final LatLng location) {
			this.circle = null;

			host.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					circle = map.addCircle(new CircleOptions().center(location.create()).fillColor(Color.TRANSPARENT).strokeColor(Color.BLACK).strokeWidth(5).zIndex(10).radius(0));
				}
			});

			this.timer = new Timer();
			this.timer.schedule(this, 0, frequency);
		}

		@Override
		public void run() {
			host.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (circle != null) {
						double radius = circle.getRadius();
						radius += sizeDelta;
						if (radius > maxSize) {
							circle.remove();
							timer.cancel();
						} else {
							circle.setRadius(radius);
						}
					}
				}
			});
		}
	}
}
