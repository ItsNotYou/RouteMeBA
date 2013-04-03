package de.unipotsdam.nexplorer.server.persistence.hibernate;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import org.hibernate.Session;

import de.unipotsdam.nexplorer.server.data.Unit;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.shared.ItemType;

public class ItemCheater extends TimerTask {

	@Override
	public void run() {
		Unit unit = new Unit();
		try {
			Session session = unit.resolve(Session.class);
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			List<Players> players = session.createCriteria(Players.class).list();
			for (Players p : players) {
				placeNewBatteryFor(p, unit.resolve(DatabaseImpl.class));
			}
		} finally {
			unit.close();
		}
	}

	private boolean hasLocation(Players p, DatabaseImpl dbAccess) {
		Setting setting = dbAccess.getSettings();
		
		if (p.getLatitude() == setting.inner().getPlayingFieldUpperLeftLatitude() && p.getLongitude() == setting.inner().getPlayingFieldUpperLeftLongitude()) {
			return false;
		} else {
			return true;			
		}
	}

	private void placeNewBatteryFor(Players p, DatabaseImpl dbAccess) {
		Items item = new Items();
		item.setCreatedAt(new Date());
		item.setItemType(ItemType.BOOSTER);
		item.setLatitude(p.getLatitude());
		item.setLongitude(p.getLongitude());
		dbAccess.persist(item);
	}
}
