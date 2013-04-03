package de.unipotsdam.nexplorer.server.time;

/**
 * Defines callbacks for timer events.
 * 
 * @author hgessner &lt;<a href="mailto:hgessner@uni-potsdam.de">hgessner@uni-potsdam.de</a>&gt;
 */
public abstract class TimerCallback implements Runnable {

	/**
	 * Called when the timer is started.
	 */
	public abstract void started();

	/**
	 * Called when the timer is paused.
	 */
	public abstract void paused();

	/**
	 * Called when the timer is resumed.
	 */
	public abstract void resumed();

	/**
	 * Called when the timer is stopped.
	 */
	public abstract void stopped();
}
