package de.unipotsdam.nexplorer.server.persistence.hibernate;

import java.util.TimerTask;

import de.unipotsdam.nexplorer.server.data.GetInternal;
import de.unipotsdam.nexplorer.server.data.Unit;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;

public class BatteryCheater extends TimerTask {

	@Override
	public void run() {
		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			for (Player node : dbAccess.getAllActiveNodesInRandomOrder()) {
				GetInternal internal = new GetInternal();
				node.execute(internal);
				if (internal.get().getBattery() < 50) {
					node.increaseBatteryBy(100.);
					node.save();
				}
			}
		} catch (Throwable t){
			System.err.println(t.getMessage());
		} finally {
			unit.close();
		}
	}
}
