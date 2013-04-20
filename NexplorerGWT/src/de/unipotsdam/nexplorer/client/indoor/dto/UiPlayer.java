package de.unipotsdam.nexplorer.client.indoor.dto;

import de.unipotsdam.nexplorer.shared.Messager;

public class UiPlayer {

	private final String name;
	private final String score;
	private final Long difficulty;

	public UiPlayer(Messager player) {
		this.name = player.getName();
		this.score = player.getScore() + "";
		this.difficulty = player.getDifficulty();
	}

	public String getName() {
		return this.name;
	}

	public String getScore() {
		return this.score;
	}

	public Long getDifficulty() {
		return this.difficulty;
	}
}
