package de.unipotsdam.nexplorer.client.android.net;

import android.location.Location;
import de.unipotsdam.nexplorer.client.android.callbacks.AjaxResult;
import de.unipotsdam.nexplorer.client.android.callbacks.Locatable;
import de.unipotsdam.nexplorer.client.android.callbacks.Loginable;

/**
 * Sendet die aktuelle Position an den Server
 * 
 * @author Hendrik Geﬂner &lt;hgessner@uni-potsdam.de&gt;
 */
public class SendLocation implements Locatable, Loginable {

	private final RestMobile rest;
	private Long playerId;
	private boolean positionRequestExecutes;

	public SendLocation(RestMobile rest) {
		this.rest = rest;

		this.playerId = null;
		this.positionRequestExecutes = false;
	}

	@Override
	public void locationChanged(Location location) {
		if (!positionRequestExecutes && location != null && playerId != null) {
			positionRequestExecutes = true;

			rest.updatePlayerPosition(playerId, location, new AjaxResult<Object>() {

				@Override
				public void success() {
					positionRequestExecutes = false;
				}

				@Override
				public void error() {
					positionRequestExecutes = false;
				}
			});
		}
	}

	@Override
	public void loggedIn(long playerId) {
		this.playerId = playerId;
	}
}
