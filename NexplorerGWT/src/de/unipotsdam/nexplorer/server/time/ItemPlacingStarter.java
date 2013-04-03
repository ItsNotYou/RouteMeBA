package de.unipotsdam.nexplorer.server.time;

import java.util.UUID;

import com.google.inject.Inject;

import de.unipotsdam.nexplorer.server.Admin;

public class ItemPlacingStarter extends StatelessTimer {

	private Admin admin;

	@Inject
	public ItemPlacingStarter(Admin admin) {
		super(new Milliseconds(10000), true);
		this.admin = admin;
	}

	@Override
	public void doRun() {
		try {
			UUID turn = UUID.randomUUID();
			logger.trace("Placing items (turn {})", turn);
			admin.placeItems();
			logger.trace("Placed items (turn {})", turn);
		} catch (Exception e) {
			logger.error("Placing items failed", e);
		}
	}
}
