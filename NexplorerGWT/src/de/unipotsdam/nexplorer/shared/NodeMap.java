package de.unipotsdam.nexplorer.shared;

import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

/**
 * sendet eine HashMap id->Player an den Client, damit dort die Verarbeitung per id schneller geht
 */
public class NodeMap {
	
	@JsonProperty("players")
	private HashMap<String, Players> players;

	public NodeMap(List<Players> nodes) {
		this.players = new HashMap<String, Players>();
		for (Players node : nodes) {
			if (node != null)
				players.put(node.id + "", node);
		}
	}

	public void setPlayers(HashMap<String, Players> players) {
		this.players = players;
	}

	public HashMap<String, Players> getPlayers() {
		return players;
	}

}
