package de.unipotsdam.nexplorer.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unipotsdam.nexplorer.shared.MessageDescription;
import de.unipotsdam.nexplorer.shared.PlayerInfo;

public class IndoorServiceImpl implements IndoorServiceAsync{

	private final IndoorServiceAsync indoor = GWT.create(IndoorService.class);
	
	@Override
	public void getPlayerInfo(int playerId, AsyncCallback<PlayerInfo> callback) {
		indoor.getPlayerInfo(playerId, callback);
		
	}

	@Override
	public void insertNewMessage(MessageDescription request,
			AsyncCallback<Boolean> callback) {
		indoor.insertNewMessage(request, callback);
		
	}

	@Override
	public void resendRouteRequest(long playerId,
			AsyncCallback<Boolean> callback) {
		indoor.resendRouteRequest(playerId, callback);
		
	}

	@Override
	public void getUpdateDisplayFrequency(AsyncCallback<Integer> callback) {
		indoor.getUpdateDisplayFrequency(callback);		
	}

	@Override
	public void policyDummy(MessageDescription desc,
			AsyncCallback<Boolean> callback) {
		indoor.policyDummy(desc, callback);
	}
}
