package de.unipotsdam.nexplorer.server.time;

import java.util.TimerTask;

public class RunnableTimerTask extends TimerTask {

	private final Runnable runnable;

	public RunnableTimerTask(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public void run() {
		runnable.run();
	}
}
