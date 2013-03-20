package de.unipotsdam.nexplorer.client.android.js;

import static android.location.LocationManager.GPS_PROVIDER;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class Geolocation {

	private static boolean IS_DEBUG = true;

	private Context context;
	private Activity host;

	public Geolocation(Context context, Activity host) {
		this.context = context;
		this.host = host;
	}

	public void clearWatch(Geolocator positionWatch) {
		if (positionWatch != null) {
			positionWatch.clear();
		}
	}

	public Geolocator watchPosition(FunctionsMobile functionsMobile, NavigatorOptions navigatorOptions) {
		if (IS_DEBUG) {
			return new DummyGeolocator(functionsMobile);
		} else {
			return new ActiveGeolocator(context, functionsMobile, host);
		}
	}

	public class ActiveGeolocator extends Geolocator {

		private LocationManager manager;
		private FunctionsMobile callback;

		public ActiveGeolocator(Context context, FunctionsMobile callback, Activity host) {
			super(callback);
			this.callback = callback;
			this.manager = registerManager(context);
			host.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					manager.requestLocationUpdates(GPS_PROVIDER, 0, 0, ActiveGeolocator.this);
				}
			});
		}

		@Override
		public void clear() {
			manager.removeUpdates(this);
		}

		private LocationManager registerManager(Context context) {
			LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			if (!manager.isProviderEnabled(GPS_PROVIDER)) {
				callback.positionError(new IOException("GPS Provider must be enabled"));
			}

			return manager;
		}
	}

	public class DummyGeolocator extends Geolocator implements Runnable {

		private LocationGenerator locations;
		private Random random;
		private TimerTaskWrapper task;

		public DummyGeolocator(FunctionsMobile callback) {
			super(callback);
			this.locations = new LocationGenerator(52.394317333573106, 13.129833250863612, 52.39410782938052, 13.131571322305263);
			this.random = new Random();
			this.task = new TimerTaskWrapper(this);

			locations.generateStartLocation(0);
			new Timer().schedule(task, 5000, 1000);
		}

		@Override
		public void run() {
			Location loc = locations.generateNextLocation(random.nextDouble() * 2, random.nextInt(91) - 45);
			super.onLocationChanged(loc);
		}

		@Override
		public void clear() {
			task.cancel();
		}

		private class TimerTaskWrapper extends TimerTask {

			private Runnable runnable;

			public TimerTaskWrapper(Runnable runnable) {
				this.runnable = runnable;
			}

			@Override
			public void run() {
				runnable.run();
			}
		}
	}
}
