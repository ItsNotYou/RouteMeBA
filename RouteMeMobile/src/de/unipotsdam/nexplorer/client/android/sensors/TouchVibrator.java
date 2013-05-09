package de.unipotsdam.nexplorer.client.android.sensors;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

public class TouchVibrator {

	private static final long DURATION_SHORT = 15;
	private Vibrator vibrator;

	public TouchVibrator(Activity host) {
		this.vibrator = (Vibrator) host.getSystemService(Service.VIBRATOR_SERVICE);
	}

	public void vibrate() {
		vibrator.vibrate(DURATION_SHORT);
	}

	public void vibrate(long milliseconds) {
		vibrator.vibrate(milliseconds);
	}
}
