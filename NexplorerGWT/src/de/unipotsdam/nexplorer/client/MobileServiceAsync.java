package de.unipotsdam.nexplorer.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unipotsdam.nexplorer.shared.PlayerLocation;

public interface MobileServiceAsync {

	void collectItem(long playerId, AsyncCallback<Boolean> callback);

	void updateNeighbours(long playerId, AsyncCallback<Boolean> callback);

	void updatePlayerPosition(PlayerLocation position, AsyncCallback<Boolean> callback);
}
