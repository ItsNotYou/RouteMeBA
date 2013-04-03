package de.unipotsdam.nexplorer.server.time;

import java.util.UUID;

import com.google.inject.Inject;

import de.unipotsdam.nexplorer.server.Admin;

public class BatteryStarter extends StatelessTimer {

	private Admin admin;

	@Inject
	public BatteryStarter(Admin admin) {
		super(new Milliseconds(5000), false);
		this.admin = admin;
	}

	@Override
	public void doRun() {
		try {
			UUID turn = UUID.randomUUID();
			logger.trace("Updating node batteries (turn {})", turn);
			admin.updateNodeBatteries();
			logger.trace("Updated node batteries (turn {})", turn);
		} catch (Exception e) {
			logger.error("Updating batteries failed", e);
		}
	}
}
