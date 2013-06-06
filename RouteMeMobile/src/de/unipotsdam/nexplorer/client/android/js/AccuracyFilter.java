package de.unipotsdam.nexplorer.client.android.js;

import android.location.Location;
import de.unipotsdam.nexplorer.client.android.sensors.GpsReceiver.PositionWatcher;

public class AccuracyFilter implements PositionWatcher {

	private PositionWatcher functionsMobile;
	private double minAccuracy;

	public AccuracyFilter(PositionWatcher functionsMobile, double minaccuracy) {
		this.functionsMobile = functionsMobile;
		this.minAccuracy = minaccuracy;
	}

	@Override
	public void positionReceived(Location location) {
		if (location.getAccuracy() > minAccuracy) {
			return;
		}

		functionsMobile.positionReceived(location);
	}

	@Override
	public void positionError(Exception error) {
		functionsMobile.positionError(error);
	}
}
