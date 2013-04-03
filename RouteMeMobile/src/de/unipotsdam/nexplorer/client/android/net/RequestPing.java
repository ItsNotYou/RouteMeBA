package de.unipotsdam.nexplorer.client.android.net;

import de.unipotsdam.nexplorer.client.android.callbacks.AjaxResult;
import de.unipotsdam.nexplorer.client.android.callbacks.Locatable;
import de.unipotsdam.nexplorer.client.android.callbacks.Loginable;
import de.unipotsdam.nexplorer.client.android.callbacks.Pingable;
import de.unipotsdam.nexplorer.client.android.commons.Location;
import de.unipotsdam.nexplorer.client.android.rest.PingResponse;

public class RequestPing implements Pingable, Locatable, Loginable {

	private final RestMobile rest;
	private Location location;
	private Integer playerId;

	public RequestPing(RestMobile rest) {
		this.rest = rest;
		this.location = null;
		this.playerId = null;
	}

	@Override
	public void locationChanged(Location location) {
		this.location = location;
	}

	@Override
	public void loggedIn(int playerId) {
		this.playerId = playerId;
	}

	@Override
	public void pingRequested() {
		if (playerId != null && location != null) {
			rest.requestPing(playerId, location, new AjaxResult<PingResponse>());
		}
	}
}
