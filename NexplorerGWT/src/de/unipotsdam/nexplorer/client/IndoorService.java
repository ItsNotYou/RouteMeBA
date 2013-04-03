package de.unipotsdam.nexplorer.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.unipotsdam.nexplorer.shared.MessageDescription;
import de.unipotsdam.nexplorer.shared.PlayerInfo;
import de.unipotsdam.nexplorer.shared.PlayerNotFoundException;

@RemoteServiceRelativePath("indoor")
public interface IndoorService extends RemoteService {

	PlayerInfo getPlayerInfo(int playerId) throws PlayerNotFoundException;

	boolean insertNewMessage(MessageDescription request) throws PlayerNotFoundException;

	boolean resendRouteRequest(long playerId);
	
	int getUpdateDisplayFrequency();
	
	public boolean policyDummy(MessageDescription desc);
}
