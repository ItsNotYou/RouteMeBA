package de.unipotsdam.nexplorer.server.persistence;

import java.util.HashMap;
import java.util.List;

import de.unipotsdam.nexplorer.server.PojoAction;
import de.unipotsdam.nexplorer.server.aodv.AodvNode;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingTableEntries;
import de.unipotsdam.nexplorer.shared.DataPacket;

public interface ProcessableDataPacket {

	public abstract HashMap<Object, PojoAction> process(long currentDataProcessingRound, long currentRoutingRound, AodvNode aodvNode, List<Neighbour> allKnownNeighbours, List<AodvRoutingTableEntries> routingTable, Setting gameSettings);

	public abstract void setOnHoldUntil(long dataProcessingRound);

	public abstract void save();

	public abstract DataPacket inner();

	public abstract AodvNode getDestination();

	public abstract AodvNode getSource();
}
