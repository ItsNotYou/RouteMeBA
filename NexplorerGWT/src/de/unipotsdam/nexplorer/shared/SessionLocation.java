package de.unipotsdam.nexplorer.shared;

import java.util.UUID;

import de.unipotsdam.nexplorer.location.shared.SavedLocation;

public class SessionLocation extends SavedLocation {

	private UUID sessionKey;

	public UUID getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(UUID sessionKey) {
		this.sessionKey = sessionKey;
	}
}
