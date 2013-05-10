package de.unipotsdam.nexplorer.client.android.sensors;

import static android.location.LocationManager.GPS_PROVIDER;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import de.unipotsdam.nexplorer.client.android.js.NavigatorOptions;

public class GpsReceiver {

	private Activity host;
	private boolean isDebug;

	public GpsReceiver(Activity host) {
		this(host, false);
	}

	public GpsReceiver(Activity host, boolean isDebug) {
		this.host = host;
		this.isDebug = isDebug;
	}

	public void clearWatch(Geolocator positionWatch) {
		if (positionWatch != null) {
			positionWatch.clear();
		}
	}

	public Geolocator watchPosition(PositionWatcher functionsMobile, NavigatorOptions navigatorOptions) {
		if (isDebug) {
			return new DummyGeolocator(functionsMobile);
		} else {
			return new ActiveGeolocator(functionsMobile, host);
		}
	}

	public class ActiveGeolocator extends Geolocator {

		private LocationManager manager;
		private PositionWatcher callback;

		public ActiveGeolocator(PositionWatcher callback, Activity host) {
			super(callback);
			this.callback = callback;
			this.manager = registerManager(host);
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

		public DummyGeolocator(PositionWatcher callback) {
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

	public abstract class Geolocator implements LocationListener {

		private PositionWatcher callback;

		public Geolocator(PositionWatcher callback) {
			this.callback = callback;
		}

		@Override
		public void onLocationChanged(Location location) {
			callback.positionReceived(location);
		}

		@Override
		public void onProviderDisabled(String arg0) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public abstract void clear();
	}

	public interface PositionWatcher {

		public void positionReceived(Location location);

		public void positionError(Exception error);
	}
}
