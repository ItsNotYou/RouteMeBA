package de.unipotsdam.nexplorer.server.rest.dto;

import java.util.HashMap;

import org.codehaus.jackson.annotate.JsonProperty;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.ItemMap;

public class NodeGameSettingsJSON {

	/**
	 * fügt den GameStats noch den aktuellen Knoten hinzu,
	 * damit beides gemeinsam zur Aktualisierung an die Mobile GUI geschickt werden
	 * kann
	 */
	private static final long serialVersionUID = -4758787633501160004L;
	
	@JsonProperty("node")
	public Players node;
	@JsonProperty("stats")
	public GameStats gameStats;
	//soll irgendeiner Tippnachricht ausgeben
	@JsonProperty("hint")
	public String hint;

	/**
	 * Diese Klasse überliefert die Daten für die Aktualisierung der Mobile Player Umgebung
	 * 
	 * @param gameStats
	 * @param node
	 */
	public NodeGameSettingsJSON(GameStats gameStats, Players node) {
		this.gameStats = gameStats;
		this.node = node;
	}

	public Players getNode() {
		return node;
	}

	public void setNode(Players node) {
		this.node = node;
	}

}
