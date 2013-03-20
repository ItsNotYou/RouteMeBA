package de.unipotsdam.nexplorer.client.android.js;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public abstract class Geolocator implements LocationListener {

	private FunctionsMobile callback;

	public Geolocator(FunctionsMobile callback) {
		this.callback = callback;
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

	public abstract void clear();
}
