package de.unipotsdam.nexplorer.server.rest;

import java.util.Date;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unipotsdam.nexplorer.server.Admin;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;
import de.unipotsdam.nexplorer.server.rest.dto.OK;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.GameStatus;
import de.unipotsdam.nexplorer.shared.Location;
import de.unipotsdam.nexplorer.shared.PlayingField;

/**
 * Verwaltet den Spielzustand. Mögliche Zustände sind in der Klasse {@link GameStatus} zu sehen.
 * 
 * @author Julian
 */
@Path("gameManager/")
public class GameImpl {

	private Admin admin;

	public GameImpl() {
		this.admin = new Admin();
	}

	/**
	 * Verändert in der DB den Zustand, so dass folgende Requests das Spielgeschehen als Datenobjekt bekommen und nicht die Startmaske.
	 */
	@POST
	@Path("resume")
	@Produces("application/json")
	public OK resumeGame() {
		admin.resumeGame();
		return new OK();
	}

	@POST
	@Path("pause")
	@Produces("application/json")
	public OK pauseGame() {
		admin.pauseGame();
		return new OK();
	}

	/**
	 * Löscht alle Einträge aus der Datenbank.
	 */
	@POST
	@Path("reset")
	@Produces("application/json")
	public OK resetGame() {
		admin.resetGame();
		return new OK();
	}

	@POST
	@Path("stop")
	@Produces("application/json")
	public OK stopGame() {
		admin.stopGame();
		return new OK();
	}

	@GET
	@Path("game_status")
	@Produces("application/json")
	public GameStats getGameStatus() {
		try {
			GameStats stats = admin.getGameStats();
			return stats;
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new GameNotStartedException("game has not started yet");
		}
		// /**
		// * TODO: switch to business logic
		// */
		// SettingsGWT settingsGwt = dummyAdmin.getDefaultGameStats();
		// settingsGwt.setRunningSince(30l);
		// settingsGwt.setIsRunning((byte)1);
		// settingsGwt.setRemainingPlayingTime(40l);
		// settingsGwt.setBonusGoal(0l);
		// settingsGwt.setUpdateDisplayIntervalTime(10l);
		// GameStats gameStats = new GameStats(settingsGwt);
		// gameStats.setGameStatus(GameStatus.ISRUNNING);
		// return (new GameStatusJSON(gameStats));
	}

	@POST
	@Path("start")
	@Produces("application/json")
	public OK startGame(@FormParam("playingTime") String playingTime, @FormParam("baseNodeRange") String baseNodeRange, @FormParam("itemCollectionRange") String itemCollectionRange, @FormParam("maxBatteries") String maxBatteries, @FormParam("maxBoosters") String maxBoosters, @FormParam("difficulty") String difficulty, @FormParam("protocol") String protocol, @FormParam("playingFieldUpperLeftLatitude") String playingFieldUpperLeftLatitude, @FormParam("playingFieldUpperLeftLongitude") String playingFieldUpperLeftLongitude, @FormParam("playingFieldLowerRightLatitude") String playingFieldLowerRightLatitude, @FormParam("playingFieldLowerRightLongitude") String playingFieldLowerRightLongitude, @FormParam("updatePositionIntervalTime") String updatePositionIntervalTime, @FormParam("updateDisplayIntervalTime") String updateDisplayIntervalTime) {
		Location lowerRight = new Location();
		lowerRight.setLongitude(Double.parseDouble(playingFieldLowerRightLongitude));
		lowerRight.setLatitude(Double.parseDouble(playingFieldLowerRightLatitude));

		Location upperLeft = new Location();
		upperLeft.setLongitude(Double.parseDouble(playingFieldUpperLeftLongitude));
		upperLeft.setLatitude(Double.parseDouble(playingFieldUpperLeftLatitude));

		PlayingField field = new PlayingField();
		field.setLowerRight(lowerRight);
		field.setUpperLeft(upperLeft);

		GameStats settings = new GameStats();
		settings.setCurrentDataRound((long) 0);
		settings.setCurrentRoutingRound((long) 0);
		// settings.setEnded(false);
		settings.setGameStatus(GameStatus.NOTSTARTED);
		settings.setPlayingField(field);
		settings.setRemainingPlaytime(Long.parseLong(playingTime));
		// settings.setRunning(false);
		settings.setRunningSince(new Date());
		admin.startGame(settings);

		// extracted for debugging purposes
		convertAjaxToJava(playingTime, baseNodeRange, itemCollectionRange, maxBatteries, maxBoosters, difficulty, protocol, playingFieldUpperLeftLatitude, playingFieldUpperLeftLongitude, playingFieldLowerRightLatitude, playingFieldLowerRightLongitude, updatePositionIntervalTime, updateDisplayIntervalTime);
		/**
		 * FIMI: Hier die businessLogic einbauen, damit ein Spiel gestartet wird playingTime=5 &baseNodeRange=9 &itemCollectionRange=4 &maxBatteries=5 &maxBoosters=2 &difficulty=1 &protocol=aodv &playingFieldUpperLeftLatitude=52.39358362957616 &playingFieldUpperLeftLongitude=13.130382746458054 &playingFieldLowerRightLatitude=52.39328409876577 &playingFieldLowerRightLongitude=13.130974173545837 &updatePositionIntervalTime=3000 &updateDisplayIntervalTime=3000 Unter Umst�nden, wird das hier zus�tzlich �ber xml rpc geregelt...
		 */
		System.out.println("Spiel wird gestartet");
		return new OK();
	}

	private void convertAjaxToJava(String playingTime, String baseNodeRange, String itemCollectionRange, String maxBatteries, String maxBoosters, String difficulty, String protocol, String playingFieldUpperLeftLatitude, String playingFieldUpperLeftLongitude, String playingFieldLowerRightLatitude, String playingFieldLowerRightLongitude, String updatePositionIntervalTime, String updateDisplayIntervalTime) {
		System.out.println("visited");
		try {
			Long updateDisplayTime = Long.parseLong(updateDisplayIntervalTime.trim());
			String updatePositionTime = updatePositionIntervalTime.trim();
			Long difficultyLong = Long.parseLong(difficulty.trim());
			Long playingTimeLong = Long.parseLong(playingTime.trim());
			Long maxBoosterLong = Long.parseLong(maxBoosters.trim());
			Long timeStarted = new Date().getTime();
			Long baseNodeRangeLong = Long.parseLong(baseNodeRange.trim());
			Long itemCollectionLong = Long.parseLong(itemCollectionRange.trim());
			Double playingFieldLeftUpperLong = Double.parseDouble(playingFieldUpperLeftLatitude.trim());
			GameStats gameStats = new GameStats(new Settings((byte) 1, timeStarted, 0l, (byte) 0, difficultyLong, playingTimeLong, 0l, protocol.trim(), baseNodeRangeLong, itemCollectionLong, playingFieldLeftUpperLong, Double.parseDouble(playingFieldUpperLeftLongitude.trim()), Double.parseDouble(playingFieldLowerRightLatitude.trim()), Double.parseDouble(playingFieldLowerRightLongitude.trim()), Long.parseLong(maxBatteries.trim()), maxBoosterLong, 0l, 0l, 0l, Long.parseLong(updatePositionTime), updateDisplayTime));
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Diese Exception ermöglicht eine lesbare Nachricht auf Browser ebene
	 * 
	 * @author Julian
	 * 
	 */
	public class GameNotStartedException extends WebApplicationException {

		public GameNotStartedException(String message) {
			super(Response.status(Response.Status.BAD_REQUEST).entity(message).type(MediaType.TEXT_PLAIN).build());
		}
	}

}
