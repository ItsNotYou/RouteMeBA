package de.unipotsdam.nexplorer.server.time;

import java.util.UUID;

import com.google.inject.Inject;

import de.unipotsdam.nexplorer.server.Admin;

public class BonusGoalStarter extends StatelessTimer {

	private Admin admin;

	@Inject
	public BonusGoalStarter(Admin admin) {
		super(new Milliseconds(8000), true);
		this.admin = admin;
	}

	@Override
	public void doRun() {
		try {
			UUID turn = UUID.randomUUID();
			logger.trace("Updating bonus goal (turn {})", turn);
			admin.updateBonusGoals();
			logger.trace("Updated bonus goal (turn {})", turn);
		} catch (Exception e) {
			logger.error("Updating bonus goal failed", e);
		}
	}
}
