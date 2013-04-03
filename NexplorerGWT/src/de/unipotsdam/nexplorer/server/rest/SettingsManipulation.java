package de.unipotsdam.nexplorer.server.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.unipotsdam.nexplorer.server.data.Unit;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;
import de.unipotsdam.nexplorer.shared.UpdatableSettings;

@Path("/settings")
public class SettingsManipulation {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("read")
	public UpdatableSettings read() {
		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Setting settings = dbAccess.getSettings();
			Settings inner = settings.inner();

			UpdatableSettings result = new UpdatableSettings();
			result.setBaseNodeRange(inner.getBaseNodeRange());
			result.setItemCollectionRange(inner.getItemCollectionRange());
			result.setMaxBatteries(inner.getMaxBatteries());
			result.setMaxBoosters(inner.getMaxBoosters());
			return result;
		} finally {
			unit.close();
		}
	}

	/**
	 * Manipulates the game settings. TODO: Adapt to splitting values between settings table, players table and items table
	 * 
	 * @param update
	 */
	@POST
	@Path("update")
	@Consumes(MediaType.APPLICATION_JSON)
	public void update(UpdatableSettings update) {
		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Setting settings = dbAccess.getSettings();
			Settings inner = settings.inner();

			if (update.getBaseNodeRange() != null)
				inner.setBaseNodeRange(update.getBaseNodeRange());

			if (update.getItemCollectionRange() != null)
				inner.setItemCollectionRange(update.getItemCollectionRange());

			if (update.getMaxBatteries() != null)
				inner.setMaxBatteries(update.getMaxBatteries());

			if (update.getMaxBoosters() != null)
				inner.setMaxBoosters(update.getMaxBoosters());
		} finally {
			unit.close();
		}
	}
}
