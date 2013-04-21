package de.unipotsdam.nexplorer.client.android.js;

import static android.graphics.Color.alpha;
import static android.graphics.Color.argb;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static java.lang.Math.max;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;

import de.unipotsdam.nexplorer.client.android.callbacks.Locatable;
import de.unipotsdam.nexplorer.client.android.callbacks.Pingable;
import de.unipotsdam.nexplorer.client.android.callbacks.Rangeable;
import de.unipotsdam.nexplorer.client.android.commons.Location;

public class RadiusBlinker implements Locatable, Pingable, Rangeable {

	private static final int frequency = 100;
	private static final double maxTime = 1500;

	private final GoogleMap map;
	private final Activity host;
	private Location location;
	private Double range;

	public RadiusBlinker(GoogleMap map, Activity host) {
		this.map = map;
		this.host = host;
	}

	@Override
	public void locationChanged(Location location) {
		this.location = location;
	}

	@Override
	public void rangeChanged(Double range) {
		this.range = range;
	}

	@Override
	public void pingRequested() {
		Location loc = location;
		Double rng = range;
		if (loc != null && rng != null) {
			new Blink(new LatLng(loc), rng);
		}
	}

	private class Blink extends TimerTask {

		private final double sizeDelta;
		private final double opacityDelta;
		private final double maxSize;
		private final Timer timer;
		private Circle circle;

		public Blink(final LatLng location, double maxSize) {
			this.maxSize = maxSize;
			this.sizeDelta = maxSize / (maxTime / frequency);
			this.opacityDelta = 255 / (maxTime / frequency);
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
		public synchronized void run() {
			host.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (circle != null) {
						double radius = circle.getRadius();
						radius += sizeDelta;

						int color = circle.getStrokeColor();
						int alpha = alpha(color);
						alpha -= opacityDelta;
						alpha = max(alpha, 0);
						color = argb(alpha, red(color), green(color), blue(color));

						if (radius > maxSize) {
							circle.remove();
							timer.cancel();
						} else {
							circle.setStrokeColor(color);
							circle.setRadius(radius);
						}
					}
				}
			});
		}
	}
}
