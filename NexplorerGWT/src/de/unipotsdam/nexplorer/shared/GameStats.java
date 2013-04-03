package de.unipotsdam.nexplorer.shared;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.gwt.user.client.rpc.GwtTransient;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;

/**
 * This class aggregates the raw settings to a more managable set of business
 * properties
 * 
 * @author Julian
 * 
 */
public class GameStats implements Serializable {

	private static final long serialVersionUID = 6220978552982706315L;

	@JsonIgnore
	private PlayingField playingField;
	@JsonProperty
	private Settings settings;

	public GameStats() {
	}

	/**
	 * 
	 * @param settings
	 */
	public GameStats(Settings settings) {
		// fill the fields for gwt
		this.settings = settings;		
		this.setPlayingField(new PlayingField(settings));
	}

	@JsonProperty("updateDisplayIntervalTime")
	public Long getUpdateDisplayIntervalTime() {
		return this.settings.getUpdateDisplayIntervalTime();
	}

	@JsonProperty("baseNodeRange")
	public long getRange() {
		return this.settings.getBaseNodeRange();
	}

	@JsonProperty("bonusGoal")
	public Long getBonusGoal() {
		return this.getSettings().getBonusGoal();
	}

	@JsonProperty("gameExists")
	public int getGameExists() {
		if (this.getGameStatus().equals(GameStatus.ISRUNNING)
				|| this.getGameStatus().equals(GameStatus.ISPAUSED)) {
			return 1;
		} else {
			return 0;
		}
	}

	@JsonProperty("didEnd")
	public int getDidEnd() {
		if (this.getGameStatus().equals(GameStatus.HASENDED)) {
			return 1;
		} else {
			return 0;
		}
	}

	@JsonProperty("playingFieldCenterLatitude")
	public Double getPlayingFieldCenterLatitude() {
		return this.getPlayingField().getCenterOfLatitude();
	}

	@JsonProperty("playingFieldCenterLongitude")
	public Double getPlayingFieldCenterLongitude() {
		return this.getPlayingField().getCenterOfLongitude();
	}

	@JsonProperty("gameDifficulty")
	public Long getGameDifficulty() {
		return settings.difficulty;
	}

	@JsonProperty("gameDifficulty")
	public void setGameDifficulty(Long gameDifficulty) {
		settings.difficulty = gameDifficulty;
	}

	public Settings getSettings() {
		return settings;
	}

	public Date getRunningSince() {
		if (this.settings.getRunningSince() != null) {
			return new Date(this.settings.getRunningSince());
		} else {
			return null;
		}
	}

	public void setRunningSince(Date runningSince) {
		this.settings.setRunningSince(runningSince.getTime());
	}

	@JsonProperty("remainingPlayingTime")
	public Long getRemainingPlaytime() {
		return this.settings.getRemainingPlayingTime();
	}

	@JsonProperty("remainingPlayingTime")
	public void setRemainingPlaytime(Long remainingPlaytime) {
		this.settings.setRemainingPlayingTime(remainingPlaytime);
	}

	public PlayingField getPlayingField() {
		return playingField;
	}

	public void setPlayingField(PlayingField playingField) {
		this.playingField = playingField;
	}

	public Long getCurrentRoutingRound() {
		return this.settings.getCurrentRoutingMessageProcessingRound();
	}

	public void setCurrentRoutingRound(Long currentRoutingRound) {
		this.settings
				.setCurrentRoutingMessageProcessingRound(currentRoutingRound);
	}

	public Long getCurrentDataRound() {
		return this.settings.getCurrentDataPacketProcessingRound();
	}

	public void setCurrentDataRound(Long currentDataRound) {
		this.settings.setCurrentDataPacketProcessingRound(currentDataRound);
	}

	@JsonProperty("isRunning")
	public int isRunning() {
		return getGameStatus().equals(GameStatus.ISRUNNING) ? 1 : 0;
	}

	/**
	 * verändert nur das running flag für mobile player
	 * 
	 * @param isRunning
	 */
	private void setRunning(Boolean isRunning) {
		if (isRunning) {
			settings.setIsRunning((byte) 1);
		} else {
			settings.setIsRunning((byte) 0);
		}

	}

	public GameStatus getGameStatus() {
		return settings.getGameState();
	}

	public Boolean isEnded() {
		if (settings.getDidEnd() == null) {
			System.err.println("getDidEnd ist null");
		}
		return settings.getDidEnd() == 1 ? true : false;
	}

	public void setGameStatus(GameStatus gameStatus) {
		settings.setGameState(gameStatus);
	}

}
