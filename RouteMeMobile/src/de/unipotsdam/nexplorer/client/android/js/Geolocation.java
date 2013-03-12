package de.unipotsdam.nexplorer.client.android.js;

import static android.location.LocationManager.GPS_PROVIDER;

import java.io.IOException;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class Geolocation {

	private Context context;

	public Geolocation(Context context) {
		this.context = context;
	}

	public void clearWatch(ActiveGeolocator positionWatch) {
		positionWatch.clear();
	}

	public ActiveGeolocator watchPosition(FunctionsMobile functionsMobile, NavigatorOptions navigatorOptions) {
		return new ActiveGeolocator(context, functionsMobile);
	}

	public class ActiveGeolocator implements LocationListener {

		private LocationManager manager;
		private FunctionsMobile callback;

		public ActiveGeolocator(Context context, FunctionsMobile callback) {
			this.callback = callback;
			this.manager = registerManager(context);
			manager.requestLocationUpdates(GPS_PROVIDER, 0, 0, this);
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
		public void onLocationChanged(Location arg0) {
			callback.positionReceived(null);
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
