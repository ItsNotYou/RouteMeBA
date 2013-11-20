package de.unipotsdam.nexplorer.server.persistence;

import java.util.List;
import java.util.Map;

import de.unipotsdam.nexplorer.server.PojoAction;
import de.unipotsdam.nexplorer.server.aodv.AodvNode;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingMessages;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingTableEntries;
import de.unipotsdam.nexplorer.shared.DataPacket;

public interface ProcessableDataPacket {

	public abstract Map<Object, PojoAction> process(long currentDataProcessingRound, long currentRoutingRound, AodvNode aodvNode, List<Neighbour> allKnownNeighbours, List<AodvRoutingTableEntries> routingTable, Setting gameSettings, List<AodvRoutingMessages> allRoutingMessages);

	public abstract void setOnHoldUntil(long dataProcessingRound);

	public abstract Map<Object, PojoAction> save();

	public abstract DataPacket inner();

	public abstract AodvNode getDestination();

	public abstract AodvNode getSource();
}
