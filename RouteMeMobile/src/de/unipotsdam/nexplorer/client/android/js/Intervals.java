package de.unipotsdam.nexplorer.client.android.js;

import static de.unipotsdam.nexplorer.client.android.js.Window.setInterval;
import static de.unipotsdam.nexplorer.client.android.js.Window.undefined;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.TimerTask;

public class Intervals {

	Geolocator positionWatch = null;

	Interval displayMarkerInterval;
	Interval gameStatusInterval;

	long updatePositionIntervalTime = 300;
	long updateDisplayIntervalTime = 300;

	private final Geolocation geolocation;

	public Intervals(Geolocation geolocation) {
		this.geolocation = geolocation;
	}

	private void startDisplayInterval(final FunctionsMobile functionsMobile) {
		if (displayMarkerInterval == undefined || displayMarkerInterval == null) {
			displayMarkerInterval = setInterval(new TimerTask() {

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
		geolocation.clearWatch(positionWatch);
		positionWatch = null;
	}

	public void restartIntervals(FunctionsMobile functionsMobile) {
		stopIntervals();
		startIntervals(functionsMobile);
	}

	void startGameStatusInterval(final FunctionsMobile functionsMobile) {
		if (gameStatusInterval == undefined || gameStatusInterval == null) {
			gameStatusInterval = setInterval(new TimerTask() {

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
			positionWatch = geolocation.watchPosition(functionsMobile, new NavigatorOptions() {

				protected void setData() {
					enableHighAccuracy = true;
					maximumAge = 0;
					timeout = 9000;
				}
			});
		}
	}

	public void setUpdateDisplayIntervalTime(int updateDisplayIntervalTime) {
		this.updateDisplayIntervalTime = updateDisplayIntervalTime;
	}
}
