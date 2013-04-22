package de.unipotsdam.nexplorer.client.android.callbacks;

public interface UIGameEvents {

	public void gamePaused();

	public void gameResumed();

	public void gameEnded();

	public void playerRemoved(RemovalReason reason);
}
