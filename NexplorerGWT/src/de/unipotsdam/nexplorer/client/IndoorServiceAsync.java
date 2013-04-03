package de.unipotsdam.nexplorer.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unipotsdam.nexplorer.shared.MessageDescription;
import de.unipotsdam.nexplorer.shared.PlayerInfo;

public interface IndoorServiceAsync {

	void getPlayerInfo(int playerId, AsyncCallback<PlayerInfo> callback);

	void insertNewMessage(MessageDescription request, AsyncCallback<Boolean> callback);

	void resendRouteRequest(long playerId, AsyncCallback<Boolean> callback);

	void getUpdateDisplayFrequency(AsyncCallback<Integer> callback);

	void policyDummy(MessageDescription desc, AsyncCallback<Boolean> callback);
}
