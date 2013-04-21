package de.unipotsdam.nexplorer.server.rest;

import static de.unipotsdam.nexplorer.server.rest.dto.LoginResultJSON.NO_GAME_CREATED;
import static de.unipotsdam.nexplorer.server.rest.dto.LoginResultJSON.SERVER_ERROR;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import de.unipotsdam.nexplorer.server.data.Unit;
import de.unipotsdam.nexplorer.server.persistence.DataFactory;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.Role;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.server.rest.dto.LoginResultJSON;

@Path("loginManager/")
public class Login {

	private LoginResultJSON login(String name, byte role) {
		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			DataFactory data = unit.resolve(DataFactory.class);
			Setting setting = dbAccess.getSettings();

			if (setting == null) {
				return new LoginResultJSON(NO_GAME_CREATED);
			}

			Player thePlayer = dbAccess.getPlayerByIdAndRole(name, role);

			if (thePlayer == null) {
				// Algemeine Spielerdaten
				Players newPlayer = new Players();
				newPlayer.setRole(role);
				newPlayer.setName(name);
				newPlayer.setBattery(100.);
				newPlayer.setScore(0l);
				newPlayer.setBaseNodeRange(setting.getBaseNodeRange());
				newPlayer.setItemCollectionRange(setting.inner().getItemCollectionRange());
				newPlayer.setDifficulty(setting.inner().getDifficulty());

				if (role == Role.NODE) {
					newPlayer.setLatitude(setting.inner().getPlayingFieldUpperLeftLatitude());
					newPlayer.setLongitude(setting.inner().getPlayingFieldUpperLeftLongitude());
					newPlayer.setSequenceNumber(1l);
					newPlayer.setHasSignalRangeBooster(0l);
					newPlayer.setPingDuration(setting.inner().getPingDuration());
				}

				thePlayer = data.create(newPlayer);
				thePlayer.save();
			}

			return new LoginResultJSON(thePlayer.getId());
		} catch (Throwable t) {
			// Some error occurred
			return new LoginResultJSON(SERVER_ERROR);
		} finally {
			unit.close();
		}
	}

	/**
	 * return the player id and logs the player in or null if some error occurred should satisfy isNaN(parseInt(data) if game not started
	 * 
	 * @param name
	 * @param isMobile
	 * @return the player id
	 */
	@POST
	@Path("login_player_indoor")
	@Produces("application/json")
	public LoginResultJSON loginPlayerIndoor(@FormParam("name") String name, @FormParam("isMobile") String isMobile) {
		LoginResultJSON result = login(name, Role.MESSAGE);
		return result;
	}

	/**
	 * return the player id and logs the player in or null if some error occurred
	 * 
	 * @param name
	 * @param isMobile
	 * @return the player id
	 */
	@POST
	@Path("login_player_mobile")
	@Produces("application/json")
	public LoginResultJSON loginPlayerMobile(@FormParam("name") String name, @FormParam("isMobile") String isMobile) {
		return login(name, Role.NODE);
	}
}
