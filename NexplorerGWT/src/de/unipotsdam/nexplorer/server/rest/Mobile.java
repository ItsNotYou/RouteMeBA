package de.unipotsdam.nexplorer.server.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import de.unipotsdam.nexplorer.server.data.GameHasNotStartedYetException;
import de.unipotsdam.nexplorer.server.data.PlayerDoesNotExistException;
import de.unipotsdam.nexplorer.server.rest.dto.NodeGameSettingsJSON;
import de.unipotsdam.nexplorer.server.rest.dto.OK;
import de.unipotsdam.nexplorer.shared.GameStatus;
import de.unipotsdam.nexplorer.shared.PlayerLocation;

@Path("mobile/")
public class Mobile {

	private de.unipotsdam.nexplorer.server.Mobile mobile;

	public Mobile() {
		this.mobile = new de.unipotsdam.nexplorer.server.Mobile();
	}

	/**
	 * get the game status as an managable object for the mobile players
	 * 
	 * @param playerId
	 * @return
	 * @throws PlayerDoesNotExistException
	 */
	@GET
	@Path("get_game_status")
	@Produces("application/json")
	public NodeGameSettingsJSON getGameStatus(@QueryParam("playerId") String playerId) throws PlayerDoesNotExistException {
		long id = Long.parseLong(playerId);
		NodeGameSettingsJSON result = mobile.getGameStatus(id);
		GameStatus gameStatus = result.gameStats.getGameStatus();
		if (gameStatus.equals(GameStatus.NOTSTARTED)) {
			throw new GameHasNotStartedYetException("game has not started, but mobile is trying to connect");
		}
		return result;
	}

	/**
	 * with this method the mobile player communicates the new geolocation
	 * 
	 * @param gpsLatitude
	 * @param gpsLongitude
	 * @param playerId
	 * @return
	 */
	@POST
	@Path("update_player_position")
	@Produces("application/json")
	public OK updatePlayerPosition(@FormParam("latitude") String gpsLatitude, @FormParam("longitude") String gpsLongitude, @FormParam("accuracy") String gpsAccuracy, @FormParam("playerId") String playerId, @FormParam("speed") String gpsSpeed, @FormParam("heading") String gpsHeading) {
		double longitude = parse(gpsLongitude);
		double latitude = parse(gpsLatitude);
		double accuracy = parse(gpsAccuracy);
		Double speed = tryParse(gpsSpeed);
		Double heading = tryParse(gpsHeading);

		// System.out.println(String.format("ID: %s\tAccuracy: %s\tSpeed: %s\tHeading: %s", playerId, accuracy, speed, heading));

		long id = Long.parseLong(playerId);
		PlayerLocation location = new PlayerLocation(id, latitude, longitude, accuracy, speed, heading);
		mobile.updatePlayerPosition(location);
		return new OK();
	}

	private Double tryParse(String value) {
		try {
			return Double.parseDouble(value);
		} catch (NullPointerException e) {
			return null;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private double parse(String value) {
		return Double.parseDouble(value);
	}

	/**
	 * the mobile player signals its intent to collect and item
	 * 
	 * @param playerId
	 * @return
	 * @throws PlayerDoesNotExistException
	 */
	@POST
	@Path("collect_item")
	@Produces("application/json")
	public OK collectItem(@FormParam("playerId") String playerId) throws PlayerDoesNotExistException {
		long id = Long.parseLong(playerId);
		if (mobile.getGameStatus(id).gameStats.getGameStatus().equals(GameStatus.HASENDED) || mobile.getGameStatus(id).gameStats.getGameStatus().equals(GameStatus.NOTSTARTED)) {
			throw new WebApplicationException(Response.Status.GONE);
		}
		mobile.collectItem(id);
		return new OK();
	}
}
