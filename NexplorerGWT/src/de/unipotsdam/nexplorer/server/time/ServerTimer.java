package de.unipotsdam.nexplorer.server.time;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

import de.unipotsdam.nexplorer.server.di.InjectLogger;

public class ServerTimer {

	@InjectLogger
	private Logger logger;
	private List<StatelessTimer> starters;

	@Inject
	public ServerTimer(BatteryStarter batteries, PlayingTimeStarter playingTime, RoutingMessageStarter routingMessage, NeighbourUpdateStarter neighbourUpdate, ItemPlacingStarter itemPlacing, BonusGoalStarter bonusGoal, SettingsUpdateStarter settingsUpdate) {
		starters = new LinkedList<StatelessTimer>();
		starters.add(itemPlacing);
		starters.add(settingsUpdate);
		starters.add(batteries);
		starters.add(playingTime);
		starters.add(neighbourUpdate);
		starters.add(routingMessage);
		starters.add(bonusGoal);
	}

	public synchronized void start() {
		logger.trace("Starting timers");
		for (StatelessTimer starter : starters) {
			starter.start();
		}
		logger.trace("Started timers");
	}

	public synchronized void pause() {
		logger.trace("Pausing timers");
		for (StatelessTimer starter : starters) {
			starter.pause();
		}
		logger.trace("Paused timers");
	}

	public synchronized void resume() {
		logger.trace("Resuming timers");
		for (StatelessTimer starter : starters) {
			starter.resume();
		}
		logger.trace("Resumed timers");
	}

	public synchronized void stop() {
		logger.trace("Stopping timers");
		for (StatelessTimer starter : starters) {
			starter.stop();
		}
		logger.trace("Stopped timers");
	}
}
