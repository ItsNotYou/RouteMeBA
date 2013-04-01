package de.unipotsdam.nexplorer.client.android.js;

import de.unipotsdam.nexplorer.client.android.commons.Location;

public interface PositionWatcher {

	public void positionReceived(Location location);

	public void positionError(Exception error);
}
