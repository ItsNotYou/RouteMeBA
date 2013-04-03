package de.unipotsdam.nexplorer.client.indoor.viewcontroller;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unipotsdam.nexplorer.client.indoor.NewMessageBinder;
import de.unipotsdam.nexplorer.client.indoor.NewRouteRequestBinder;
import de.unipotsdam.nexplorer.client.indoor.PlayerInfoBinder;
import de.unipotsdam.nexplorer.client.indoor.ResetPlayerMessageBinder;
import de.unipotsdam.nexplorer.shared.Aodv;
import de.unipotsdam.nexplorer.shared.PlayerInfo;

public class PlayerInfoUpdater implements AsyncCallback<PlayerInfo> {

	private PlayerInfoBinder playerInfoBinder;
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
		this.playerInfoBinder.updatePlayerInfos(result);
		// update button
		DivElement divElement = (DivElement) this.playerInfoBinder.getStatus();
		if (result.getDataPacketSend() != null) {
			Byte status = result.getDataPacketSend().getStatus();
			switch (status) {
			case Aodv.DATA_PACKET_STATUS_NODE_BUSY:
			case Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE:
			case Aodv.DATA_PACKET_STATUS_UNDERWAY:
			case Aodv.DATA_PACKET_STATUS_ERROR:
				if (!(buttonSetShown.equals(ButtonSetShown.Other))) {
					removeShownButton();
					showButtonsWhileMessageUnderway(divElement);
				}				
				buttonSetShown = ButtonSetShown.Other;
				break;
			default:
				if (!(buttonSetShown.equals(ButtonSetShown.NewMessage))) {
					removeShownButton();			
					showNewMessageButton(divElement);					
				}				
				buttonSetShown = ButtonSetShown.NewMessage;
			}
		}		
	}

	private void showButtonsWhileMessageUnderway(DivElement divElement) {
		divElement.appendChild((new NewRouteRequestBinder().getElement()));
		divElement.appendChild((new ResetPlayerMessageBinder().getElement()));
	}

	private void showNewMessageButton(DivElement divElement) {
		divElement.appendChild(new NewMessageBinder().getElement());
	}

	private void removeShownButton() {
		doRemoveButtons();
		// ja, das ist Absicht!!
		doRemoveButtons();
	}

	private void doRemoveButtons() {
		if (this.playerInfoBinder.getStatus().hasChildNodes()) {
			this.playerInfoBinder.getStatus().getChild(0).removeFromParent();
		}
	}

}
