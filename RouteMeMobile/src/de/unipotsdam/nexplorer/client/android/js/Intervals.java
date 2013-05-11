package de.unipotsdam.nexplorer.client.android.js;

import android.os.Handler;
import de.unipotsdam.nexplorer.client.android.sensors.GpsReceiver;
import de.unipotsdam.nexplorer.client.android.sensors.GpsReceiver.Geolocator;

public class Intervals {

	Geolocator positionWatch = null;
	private Handler handler;

	Interval displayMarkerInterval;
	Interval gameStatusInterval;

	long updatePositionIntervalTime = 300;
	long updateDisplayIntervalTime = 300;

	private final GpsReceiver geolocation;

	public Intervals(GpsReceiver geolocation, Handler handler) {
		this.geolocation = geolocation;
		this.handler = handler;
	}

	private void startDisplayInterval(final FunctionsMobile functionsMobile) {
		if (displayMarkerInterval == null || displayMarkerInterval == null) {
			displayMarkerInterval = new Interval(handler, 500) {

				@Override
				public void call() {
					functionsMobile.updateDisplay();
				}
			};
		}
	}

	void stopIntervals() {
	}

	public void restartIntervals(FunctionsMobile functionsMobile) {
		stopIntervals();
		startIntervals(functionsMobile);
	}

	public void startGameStatusInterval(final FunctionsMobile functionsMobile) {
		if (gameStatusInterval == null || gameStatusInterval == null) {
			gameStatusInterval = new Interval(handler, updateDisplayIntervalTime) {

				@Override
				public void call() {
					functionsMobile.updateGameStatus(true);
				}
			};
		}
	}

	/**
	 * bewirkt, dass das Display regelm‰ﬂig aktualisiert wird und die aktuelle Position an den Server gesendet wird
	 * 
	 * @param functionsMobile
	 *            TODO
	 */
	void startIntervals(FunctionsMobile functionsMobile) {
		startGameStatusInterval(functionsMobile);
		startDisplayInterval(functionsMobile);
	}

	void ensurePositionWatch(FunctionsMobile functionsMobile) {
		if (positionWatch == null) {
			positionWatch = geolocation.watchPosition(functionsMobile);
		}
	}

	public void setUpdateDisplayIntervalTime(int updateDisplayIntervalTime) {
		this.updateDisplayIntervalTime = updateDisplayIntervalTime;
	}
}
