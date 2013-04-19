package de.unipotsdam.nexplorer.client.indoor.viewcontroller;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unipotsdam.nexplorer.client.indoor.PlayerInfoBinder;
import de.unipotsdam.nexplorer.client.indoor.view.messaging.UiInfo;
import de.unipotsdam.nexplorer.shared.Aodv;
import de.unipotsdam.nexplorer.shared.PlayerInfo;

public class PlayerInfoUpdater implements AsyncCallback<PlayerInfo> {

	private final PlayerInfoBinder playerInfoBinder;
	private static ButtonSetShown buttonSetShown = ButtonSetShown.NewMessage;

	public PlayerInfoUpdater(PlayerInfoBinder playerInfoBinder) {
		this.playerInfoBinder = playerInfoBinder;
	}

	@Override
	public void onFailure(Throwable caught) {
		GWT.log("Did not receive playerInfos from the server");
		GWT.log(caught.getMessage());
	}

	@Override
	public void onSuccess(PlayerInfo result) {
		// do nothing if the result is null
		if (result.getPlayer() == null) {
			return;
		}
		// update other parts of the view
		updateUiWith(result);
		// update button
		if (result.getDataPacketSend() != null) {
			Byte status = result.getDataPacketSend().getStatus();
			switch (status) {
			case Aodv.DATA_PACKET_STATUS_NODE_BUSY:
			case Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE:
			case Aodv.DATA_PACKET_STATUS_UNDERWAY:
			case Aodv.DATA_PACKET_STATUS_ERROR:
				if (!(buttonSetShown.equals(ButtonSetShown.Other))) {
					playerInfoBinder.switchToButtonState(ButtonSetShown.Other);
				}
				buttonSetShown = ButtonSetShown.Other;
				break;
			default:
				if (!(buttonSetShown.equals(ButtonSetShown.NewMessage))) {
					playerInfoBinder.switchToButtonState(ButtonSetShown.NewMessage);
				}
				buttonSetShown = ButtonSetShown.NewMessage;
			}
		}
	}

	private void updateUiWith(PlayerInfo result) {
		UiInfo info = new UiInfo(result);
		this.playerInfoBinder.updatePlayerInfos(info);
	}
}
