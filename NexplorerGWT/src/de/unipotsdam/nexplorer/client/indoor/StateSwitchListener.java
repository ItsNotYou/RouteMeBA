package de.unipotsdam.nexplorer.client.indoor;

import de.unipotsdam.nexplorer.client.indoor.viewcontroller.ButtonSetShown;
import de.unipotsdam.nexplorer.shared.DataPacket;

public interface StateSwitchListener {

	public void stateSwitchedTo(ButtonSetShown state, DataPacket reason);
}
