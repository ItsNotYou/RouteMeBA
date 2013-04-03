package de.unipotsdam.nexplorer.server.time;

import com.google.inject.Inject;

import de.unipotsdam.nexplorer.server.Admin;

public class RoutingMessageStarter extends StatelessTimer {

	private Admin admin;

	@Inject
	public RoutingMessageStarter(Admin admin) {
		super(new Milliseconds(1000), false);
		this.admin = admin;
	}

	@Override
	public void doRun() {
		try {
			admin.aodvProcessRoutingMessages();
			admin.aodvProcessDataPackets();
		} catch (Exception e) {
			// Oh my god, oh my god, the sky is falling!
			logger.error("Exception while routing", e); // Let the skyfall
		}
	}
}
