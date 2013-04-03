package de.unipotsdam.nexplorer.server.time;

import java.util.UUID;

import com.google.inject.Inject;

import de.unipotsdam.nexplorer.server.Admin;

public class PlayingTimeStarter extends StatelessTimer {

	private Admin admin;
	private static final int interval = 3000;

	@Inject
	public PlayingTimeStarter(Admin admin) {
		super(new Milliseconds(interval), false);
		this.admin = admin;
	}

	@Override
	public void doRun() {
		try {
			UUID turn = UUID.randomUUID();
			logger.trace("Updating remaining playing time (turn {})", turn);
			admin.updateRemainingPlayingTime(new Milliseconds(interval));
			logger.trace("Updated remaining playing time (turn {})", turn);
		} catch (Exception e) {
			logger.error("Updating playing time failed", e);
		}
	}
}
