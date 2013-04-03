package de.unipotsdam.nexplorer.server.persistence.hibernate;

import java.util.List;
import java.util.TimerTask;

import de.unipotsdam.nexplorer.server.Mobile;
import de.unipotsdam.nexplorer.server.aodv.AodvDataPacket;
import de.unipotsdam.nexplorer.server.data.Unit;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.shared.PlayerLocation;

public class RunningPlayer extends TimerTask {

	private long runnerId;
	private boolean jumped;
	private Mobile mobile;

	public RunningPlayer(long runnerId) {
		this.runnerId = runnerId;
		jumped = false;
		mobile = new Mobile();
	}

	@Override
	public void run() {
		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			if (dbAccess.getSettings() == null) {
				return;
			}

			Player runner = dbAccess.getPlayerById(runnerId);
			List<AodvDataPacket> packets = dbAccess.getAllDataPacketsSortedByDate(runner);
			if (!packets.isEmpty() && !jumped) {
				mobile.updatePlayerPosition(new PlayerLocation(runnerId, 52.39365, 13.130513, 5.));
				jumped = true;
			} else if (packets.isEmpty() && jumped) {
				mobile.updatePlayerPosition(new PlayerLocation(runnerId, 52.39345, 13.130513, 5.));
				jumped = false;
			}
		} finally {
			unit.close();
		}
	}
}
