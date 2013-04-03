package de.unipotsdam.nexplorer.server.time;

import java.util.Timer;

import org.apache.logging.log4j.Logger;

import de.unipotsdam.nexplorer.server.di.InjectLogger;

/**
 * Provides a basic skeleton for timer based tasks. Manages calls to a {@link Timer} and provides an adapter for the {@link TimerCallback}.
 * 
 * @author hgessner &lt;<a href="mailto:hgessner@uni-potsdam.de">hgessner@uni-potsdam.de</a>&gt;
 */
public abstract class StatelessTimer extends TimerCallback {

	@InjectLogger
	protected Logger logger;
	private final Milliseconds delay;
	private final boolean runAtStartup;

	private Timer timer;
	private boolean isRunning;

	/**
	 * @param delay
	 *            Time between successive task executions.
	 * @param runAtStartup
	 *            If {@code true}, {@link #run()} is called when a {@link Timer} is scheduled.
	 * @see Timer#schedule(java.util.TimerTask, long, long)
	 */
	protected StatelessTimer(Milliseconds delay, boolean runAtStartup) {
		this.timer = new Timer();
		this.delay = delay;
		this.runAtStartup = runAtStartup;
		this.isRunning = false;
	}

	private void createTimer() {
		timer.cancel();
		timer = new Timer();
		// If set call run() and wait for its completion
		if (runAtStartup)
			this.run();
	}

	private void scheduleTimer() {
		timer.schedule(new RunnableTimerTask(this), delay.value(), delay.value());
	}

	/**
	 * Starts the timer. Schedules the {@link Timer} and calls {@link #started()}. If {@code runAtStartup} was set to {@code true}, then {@link #run()} (and with it {@link #doRun()} is called. Timer scheduling happens after {@link #run()} call completes.
	 * 
	 * @see Timer#schedule(java.util.TimerTask, long, long)
	 */
	public synchronized void start() {
		logger.trace("Starting timer");
		createTimer();
		this.started();
		scheduleTimer();
		logger.trace("Started timer");
	}

	/**
	 * Pauses the timer. Cancels the scheduled {@link Timer} and calls {@link #paused()}.
	 * 
	 * @see Timer#cancel()
	 */
	public synchronized void pause() {
		logger.trace("Pausing timer");
		timer.cancel();
		this.paused();
		logger.trace("Paused timer");
	}

	/**
	 * Resumes the timer. Same as {@link #start()}, but calls {@link #resumed()}.
	 */
	public synchronized void resume() {
		logger.trace("Resuming timer");
		createTimer();
		this.resumed();
		scheduleTimer();
		logger.trace("Resumed timer");
	}

	/**
	 * Stops the timer. Same as {@link #pause()}, but calls {@link #stopped()}.
	 */
	public synchronized void stop() {
		logger.trace("Ending timer");
		timer.cancel();
		this.stopped();
		logger.trace("Ended timer");
	}

	/**
	 * Calls {@link #doRun()}. Skips additional calls to {@link #doRun()} if the instance is already executing the method. This behaviour is useful to prevent unwanted stacking of time intensive task if the interval between two calls is less than the exection time of one call.
	 */
	@Override
	public void run() {
		if (isRunning)
			return;

		try {
			isRunning = true;
			doRun();
		} finally {
			isRunning = false;
		}
	}

	/**
	 * Called by {@link #run()}.
	 */
	protected void doRun() {
	}

	@Override
	public void started() {
	}

	@Override
	public void paused() {
	}

	@Override
	public void resumed() {
	}

	@Override
	public void stopped() {
	}
}
