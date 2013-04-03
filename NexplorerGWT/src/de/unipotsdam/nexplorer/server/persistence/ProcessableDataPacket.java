package de.unipotsdam.nexplorer.server.persistence;


import de.unipotsdam.nexplorer.server.aodv.AodvNode;
import de.unipotsdam.nexplorer.shared.DataPacket;

public interface ProcessableDataPacket {

	public abstract void process(long currentDataProcessingRound, AodvNode aodvNode);

	public abstract void setOnHoldUntil(long dataProcessingRound);

	public abstract void save();

	public abstract DataPacket inner();

	public abstract AodvNode getDestination();

	public abstract AodvNode getSource();
}
