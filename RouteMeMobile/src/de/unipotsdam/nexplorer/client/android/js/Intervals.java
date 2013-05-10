package de.unipotsdam.nexplorer.client.android.js;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.TimerTask;

import de.unipotsdam.nexplorer.client.android.sensors.GpsReceiver;
import de.unipotsdam.nexplorer.client.android.sensors.GpsReceiver.Geolocator;

public class Intervals {

	Geolocator positionWatch = null;

	Interval displayMarkerInterval;
	Interval gameStatusInterval;

	long updatePositionIntervalTime = 300;
	long updateDisplayIntervalTime = 300;

	private final GpsReceiver geolocation;

	public Intervals(GpsReceiver geolocation) {
		this.geolocation = geolocation;
	}

	private void startDisplayInterval(final FunctionsMobile functionsMobile) {
		if (displayMarkerInterval == null || displayMarkerInterval == null) {
			displayMarkerInterval = new Interval().set(new TimerTask() {

				@Override
				public void run() {
					try {
						functionsMobile.updateDisplay();
					} catch (Throwable e) {
						StringWriter w = new StringWriter();
						e.printStackTrace(new PrintWriter(w));
						String message = w.toString();
						e.toString();
					}
				}
			}, 500);
		}
	}

	void stopIntervals() {
	}

	public void restartIntervals(FunctionsMobile functionsMobile) {
		stopIntervals();
		startIntervals(functionsMobile);
	}

	void startGameStatusInterval(final FunctionsMobile functionsMobile) {
		if (gameStatusInterval == null || gameStatusInterval == null) {
			gameStatusInterval = new Interval().set(new TimerTask() {

				@Override
				public void run() {
					try {
						functionsMobile.updateGameStatus(true);
					} catch (Throwable e) {
						e.toString();
					}
				}
			}, updateDisplayIntervalTime);
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
