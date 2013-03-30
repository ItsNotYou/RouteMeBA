package de.unipotsdam.nexplorer.client.android.rest;

public class Stats {

	public Settings settings;

	private String remainingPlayingTime;
	private String gameExists;
	private String baseNodeRange;
	private String gameDifficulty;
	private String didEnd;

	public String getRemainingPlayingTime() {
		return remainingPlayingTime;
	}

	public void setRemainingPlayingTime(String remainingPlayingTime) {
		this.remainingPlayingTime = remainingPlayingTime;
	}

	public String getGameExists() {
		return gameExists;
	}

	public void setGameExists(String gameExists) {
		this.gameExists = gameExists;
	}

	public String getBaseNodeRange() {
		return baseNodeRange;
	}

	public void setBaseNodeRange(String baseNodeRange) {
		this.baseNodeRange = baseNodeRange;
	}

	public String getGameDifficulty() {
		return gameDifficulty;
	}

	public void setGameDifficulty(String gameDifficulty) {
		this.gameDifficulty = gameDifficulty;
	}

	public String getDidEnd() {
		return didEnd;
	}

	public void setDidEnd(String didEnd) {
		this.didEnd = didEnd;
	}
}
