package de.unipotsdam.nexplorer.client.android.js;

import java.util.Timer;
import java.util.TimerTask;

public class Interval {

	private Timer timer = null;

	public void clear() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void set(TimerTask task, long millisecond) {
		clear();

		timer = new Timer();
		timer.schedule(task, millisecond);
	}
}
