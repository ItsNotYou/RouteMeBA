package de.unipotsdam.nexplorer.server.aodv;

import de.unipotsdam.nexplorer.server.persistence.PlayerInternal;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class AsDestination implements PlayerInternal {

	private AodvDataPackets packet;

	public AsDestination(AodvDataPackets newMessage) {
		this.packet = newMessage;
	}

	@Override
	public void execute(Players inner) {
		packet.setPlayersByDestinationId(inner);
	}
}
