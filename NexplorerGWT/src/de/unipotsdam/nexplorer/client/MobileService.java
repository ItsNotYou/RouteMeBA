package de.unipotsdam.nexplorer.client;

import com.google.gwt.user.client.rpc.RemoteService;

import de.unipotsdam.nexplorer.shared.PlayerLocation;

/**
 * In the current setup the mobile page is
 * only updated via ajax calls to the 
 * jersey part of the server 
 * @author Julian
 *
 */
public interface MobileService extends RemoteService {

	boolean collectItem(long playerId);

	boolean updateNeighbours(long playerId);

	boolean updatePlayerPosition(PlayerLocation position);
}
