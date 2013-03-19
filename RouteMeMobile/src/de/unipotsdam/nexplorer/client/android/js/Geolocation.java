package de.unipotsdam.nexplorer.client.android.js;

import static android.location.LocationManager.GPS_PROVIDER;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class Geolocation {

	private Context context;
	private Activity host;

	public Geolocation(Context context, Activity host) {
		this.context = context;
		this.host = host;
	}

	public void clearWatch(ActiveGeolocator positionWatch) {
		if (positionWatch != null) {
			positionWatch.clear();
		}
	}

	public ActiveGeolocator watchPosition(FunctionsMobile functionsMobile, NavigatorOptions navigatorOptions) {
		return new ActiveGeolocator(context, functionsMobile, host);
	}

	public class ActiveGeolocator implements LocationListener {

		private LocationManager manager;
		private FunctionsMobile callback;

		public ActiveGeolocator(Context context, FunctionsMobile callback, Activity host) {
			this.callback = callback;
			this.manager = registerManager(context);
			host.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					manager.requestLocationUpdates(GPS_PROVIDER, 0, 0, ActiveGeolocator.this);
				}
			});
		}

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

		@Override
		public void onLocationChanged(Location location) {
			de.unipotsdam.nexplorer.client.android.support.Location result = new de.unipotsdam.nexplorer.client.android.support.Location();
			result.setLatitude(location.getLatitude());
			result.setLongitude(location.getLongitude());
			result.setAccuracy(location.getAccuracy());
			result.setHeading(location.getBearing());
			result.setSpeed(location.getSpeed());

			callback.positionReceived(result);
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
	}
}
