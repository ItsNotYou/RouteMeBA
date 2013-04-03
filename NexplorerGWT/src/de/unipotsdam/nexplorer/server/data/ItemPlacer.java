package de.unipotsdam.nexplorer.server.data;

import java.util.Date;
import java.util.List;
import java.util.Random;

import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Item;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.shared.ItemType;
import de.unipotsdam.nexplorer.shared.Location;

public class ItemPlacer {

	private final DatabaseImpl dbAccess;

	public ItemPlacer(DatabaseImpl dbAccess) {
		this.dbAccess = dbAccess;
	}

	public void placeItems() {
		Setting gameSettings = dbAccess.getSettings();

		List<Item> batteries = dbAccess.getAllBatteries();
		if (batteries.size() < gameSettings.inner().getMaxBatteries()) {
			for (int i = 1; i <= gameSettings.inner().getMaxBatteries() - batteries.size(); i++) {
				Location latLong = getLatLongForNewItem(gameSettings, dbAccess);

				Items item = new Items();
				item.setItemType(ItemType.BATTERY);
				item.setLatitude(latLong.getLatitude());
				item.setLongitude(latLong.getLongitude());
				item.setCreated(new Date().getTime());
				dbAccess.persist(item);
			}
		}

		List<Item> boosters = dbAccess.getAllBoosters();
		if (boosters.size() < gameSettings.inner().getMaxBoosters()) {
			for (int i = 1; i <= gameSettings.inner().getMaxBoosters() - boosters.size(); i++) {
				Location latLong = getLatLongForNewItem(gameSettings, dbAccess);

				Items item = new Items();
				item.setItemType(ItemType.BOOSTER);
				item.setLatitude(latLong.getLatitude());
				item.setLongitude(latLong.getLongitude());
				item.setCreated(new Date().getTime());
				dbAccess.persist(item);
			}
		}
	}

	private double random_float(double min, double max) {
		return min + new Random().nextFloat() * Math.abs(max - min);
	}

	public Location getLatLongForNewItem(Setting gameSettings, DatabaseImpl db) {
		double latitude = random_float(gameSettings.inner().getPlayingFieldLowerRightLatitude(), gameSettings.inner().getPlayingFieldUpperLeftLatitude());
		double longitude = random_float(gameSettings.inner().getPlayingFieldUpperLeftLongitude(), gameSettings.inner().getPlayingFieldLowerRightLongitude());

		List<Item> query = db.getAllItemsNear(new Location(latitude, longitude), gameSettings.inner().getItemCollectionRange());

		Location latLong;
		// Prüfe ob anderer Gegenstand zu nah an diesen Koordinaten ist
		if (query.size() > 0) {
			latLong = getLatLongForNewItem(gameSettings, db);
		} else {
			latLong = new Location(latitude, longitude);
		}

		return latLong;
	}

}
