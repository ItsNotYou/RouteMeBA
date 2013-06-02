package de.unipotsdam.nexplorer.client.android.js;

import android.os.Handler;

public abstract class Interval implements Runnable {

	private Handler handler;
	private long milliseconds;
	private boolean isCancelled;

	public Interval(Handler handler, long milliseconds) {
		this.handler = handler;
		this.milliseconds = milliseconds;
		this.isCancelled = true;
	}

	public void start() {
		if (isCancelled) {
			isCancelled = false;
			run();
		}
	}

	@Override
	public void run() {
		if (!isCancelled) {
			try {
				handler.postDelayed(this, milliseconds);
				call();
			} catch (Throwable t) {
			}
		}
	}

	public void cancel() {
		this.isCancelled = true;
	}

	public abstract void call();
}
