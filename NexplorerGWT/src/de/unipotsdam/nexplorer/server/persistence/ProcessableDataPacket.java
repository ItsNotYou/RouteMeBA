package de.unipotsdam.nexplorer.server.persistence;

import java.util.List;

import de.unipotsdam.nexplorer.server.aodv.AodvNode;
import de.unipotsdam.nexplorer.shared.DataPacket;

public interface ProcessableDataPacket {

	public abstract void process(long currentDataProcessingRound, long currentRoutingRound, AodvNode aodvNode, List<Neighbour> allKnownNeighbours);

	public abstract void setOnHoldUntil(long dataProcessingRound);

	public abstract void save();

	public abstract DataPacket inner();

	public abstract AodvNode getDestination();

	public abstract AodvNode getSource();
}
