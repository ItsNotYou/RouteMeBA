package de.unipotsdam.nexplorer.server.aodv;

import de.unipotsdam.nexplorer.server.persistence.PlayerInternal;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class AsOwner implements PlayerInternal {

	private AodvDataPackets packet;

	public AsOwner(AodvDataPackets packet) {
		this.packet = packet;
	}

	@Override
	public void execute(Players inner) {
		packet.setPlayersByOwnerId(inner);
	}
}
