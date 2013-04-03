package de.unipotsdam.nexplorer.server.aodv;

import de.unipotsdam.nexplorer.shared.Aodv;
import de.unipotsdam.nexplorer.shared.DataPacket;

class Destination {

	private DataPacket message;

	public Destination(DataPacket message) {
		this.message = message;
	}

	public void toDestination() {
		// Nachricht auf ihren Weg schicken
		message.setStatus(Aodv.DATA_PACKET_STATUS_UNDERWAY);
	}
}