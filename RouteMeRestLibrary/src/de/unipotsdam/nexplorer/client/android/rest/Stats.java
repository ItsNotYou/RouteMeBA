package de.unipotsdam.nexplorer.client.android.rest;

public class Stats {

	public Settings settings;

	private Long remainingPlayingTime;
	private Integer gameExists;
	private Integer baseNodeRange;
	private String gameDifficulty;
	private Integer didEnd;

	public Long getRemainingPlayingTime() {
		return remainingPlayingTime;
	}

	public void setRemainingPlayingTime(Long remainingPlayingTime) {
		this.remainingPlayingTime = remainingPlayingTime;
	}

	public Boolean isGameExistingBoolean() {
		if (gameExists == null) {
			return null;
		} else {
			return gameExists != 0;
		}
	}

	public Integer getGameExists() {
		return gameExists;
	}

	public void setGameExists(Integer gameExists) {
		this.gameExists = gameExists;
	}

	public Integer getBaseNodeRange() {
		return baseNodeRange;
	}

	public void setBaseNodeRange(Integer baseNodeRange) {
		this.baseNodeRange = baseNodeRange;
	}

	public String getGameDifficulty() {
		return gameDifficulty;
	}

	public void setGameDifficulty(String gameDifficulty) {
		this.gameDifficulty = gameDifficulty;
	}

	public Boolean hasEndedBoolean() {
		if (didEnd == null) {
			return null;
		} else {
			return didEnd != 0;
		}
	}

	public Integer getDidEnd() {
		return didEnd;
	}

	public void setDidEnd(Integer didEnd) {
		this.didEnd = didEnd;
	}
}
