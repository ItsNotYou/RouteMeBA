package de.unipotsdam.nexplorer.client.indoor.view.messaging;

import de.unipotsdam.nexplorer.shared.Messager;

public class UiPlayer {

	private final String name;
	private final String score;

	public UiPlayer(Messager player) {
		this.name = player.getName();
		this.score = player.getScore() + "";
	}

	public String getName() {
		return this.name;
	}

	public String getScore() {
		return this.score;
	}
}
