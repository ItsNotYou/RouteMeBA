package de.unipotsdam.nexplorer.server.aodv;

import de.unipotsdam.nexplorer.server.persistence.PlayerInternal;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class AsCurrent implements PlayerInternal {

	private AodvDataPackets packet;

	public AsCurrent(AodvDataPackets packet) {
		this.packet = packet;
	}

	@Override
	public void execute(Players inner) {
		packet.setPlayersByCurrentNodeId(inner);
	}
}
