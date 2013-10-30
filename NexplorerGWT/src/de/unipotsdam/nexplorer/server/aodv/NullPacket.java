package de.unipotsdam.nexplorer.server.aodv;

import java.util.List;

import de.unipotsdam.nexplorer.server.persistence.Neighbour;
import de.unipotsdam.nexplorer.server.persistence.ProcessableDataPacket;
import de.unipotsdam.nexplorer.shared.DataPacket;

public class NullPacket implements ProcessableDataPacket {

	@Override
	public void process(long currentDataProcessingRound, AodvNode aodvNode, List<Neighbour> allKnownNeighbours) {
	}

	@Override
	public void setOnHoldUntil(long dataProcessingRound) {
	}

	@Override
	public void save() {
	}

	@Override
	public DataPacket inner() {
		return null;
	}

	public AodvNode getDestination() {
		return null;
	}

	public AodvNode getSource() {
		return null;
	}
}
