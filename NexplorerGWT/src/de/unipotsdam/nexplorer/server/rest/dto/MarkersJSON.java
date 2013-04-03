package de.unipotsdam.nexplorer.server.rest.dto;

import java.util.HashMap;

import org.codehaus.jackson.annotate.JsonProperty;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.server.rest.JSONable;
import de.unipotsdam.nexplorer.shared.DataPacketLocatable;

public class MarkersJSON extends JSONable<MarkersJSON> {

	@JsonProperty("playerMarkers")
	HashMap<Long, Players> playerMarkers;
	@JsonProperty("messageMarkers")
	HashMap<Long, DataPacketLocatable> messageMarkers;

	public MarkersJSON(HashMap<Long, Players> playerMarkers, HashMap<Long, DataPacketLocatable> messageMarkers) {
		this.playerMarkers = playerMarkers;
		this.messageMarkers = messageMarkers;
	}

	public HashMap<Long, Players> getPlayerMarkers() {
		return playerMarkers;
	}

	public HashMap<Long, DataPacketLocatable> getMessageMarkers() {
		return messageMarkers;
	}
}
