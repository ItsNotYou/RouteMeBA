package de.unipotsdam.nexplorer.server.aodv;

import de.unipotsdam.nexplorer.server.persistence.PlayerInternal;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class AsSource implements PlayerInternal {

	private AodvDataPackets packet;

	public AsSource(AodvDataPackets packet) {
		this.packet = packet;
	}

	@Override
	public void execute(Players inner) {
		packet.setPlayersBySourceId(inner);
	}
}
