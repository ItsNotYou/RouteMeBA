package de.unipotsdam.nexplorer.server.time;

import java.util.UUID;

import com.google.inject.Inject;

import de.unipotsdam.nexplorer.server.Admin;

public class NeighbourUpdateStarter extends StatelessTimer {

	private Admin admin;

	@Inject
	public NeighbourUpdateStarter(Admin admin) {
		super(new Milliseconds(500), false);
		this.admin = admin;
	}

	@Override
	public void doRun() {
		try {
			long begin = System.currentTimeMillis();
			UUID turn = UUID.randomUUID();
			logger.trace("Updating neighbours (turn {})", turn);
			admin.updateNeighbours();
			logger.trace("Updated neighbours (turn {})", turn);
			long end = System.currentTimeMillis();
			System.out.println("Updating neighbours took " + (end - begin) + "ms");
		} catch (Exception e) {
			logger.error("Updating neighbours failed", e);
		}
	}
}
