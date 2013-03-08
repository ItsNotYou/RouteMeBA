package de.unipotsdam.nexplorer.client.android.js;

import de.unipotsdam.nexplorer.client.android.net.Location;

public interface PositionWatcher {

	public void positionReceived(Location location);

	public void positionError(Exception error);
}
