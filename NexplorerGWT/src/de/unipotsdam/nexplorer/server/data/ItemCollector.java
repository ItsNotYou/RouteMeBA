package de.unipotsdam.nexplorer.server.data;

import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

import de.unipotsdam.nexplorer.server.di.InjectLogger;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Item;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.shared.ItemType;

public class ItemCollector {

	@InjectLogger
	private Logger logger;
	private DatabaseImpl dbAccess;

	@Inject
	public ItemCollector(DatabaseImpl dbAccess) {
		this.dbAccess = dbAccess;
	}

	public void collectFor(long playerId) {
		logger.info("Collect item for player {}", playerId);

		Player thePlayer = dbAccess.getPlayerById(playerId);
		Item theItem = thePlayer.getItemInCollectionRange();

		if (theItem != null) {
			if (theItem.inner().getItemType() == ItemType.BATTERY) {
				thePlayer.increaseBatteryBy(20.);
			} else if (theItem.inner().getItemType() == ItemType.BOOSTER) {
				thePlayer.activateSignalRangeBooster();
			}
			theItem.delete();
			thePlayer.save();
		}
	}
}
