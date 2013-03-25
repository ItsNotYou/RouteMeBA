package de.unipotsdam.nexplorer.client.android.net;

public class Stats {

	public Settings settings;

	private String remainingPlayingTime;
	private String gameExists;
	private String baseNodeRange;
	private String gameDifficulty;
	private String didEnd;
	private String playingFieldCenterLatitude;
	private String playingFieldCenterLongitude;

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

	public String getPlayingFieldCenterLatitude() {
		return playingFieldCenterLatitude;
	}

	public void setPlayingFieldCenterLatitude(String playingFieldCenterLatitude) {
		this.playingFieldCenterLatitude = playingFieldCenterLatitude;
	}

	public String getPlayingFieldCenterLongitude() {
		return playingFieldCenterLongitude;
	}

	public void setPlayingFieldCenterLongitude(String playingFieldCenterLongitude) {
		this.playingFieldCenterLongitude = playingFieldCenterLongitude;
	}
}
