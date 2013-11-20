package de.unipotsdam.nexplorer.server.aodv;

import java.util.List;
import java.util.Map;

import de.unipotsdam.nexplorer.server.PojoAction;
import de.unipotsdam.nexplorer.server.data.Maps;
import de.unipotsdam.nexplorer.server.persistence.Neighbour;
import de.unipotsdam.nexplorer.server.persistence.ProcessableDataPacket;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingTableEntries;
import de.unipotsdam.nexplorer.shared.DataPacket;

public class NullPacket implements ProcessableDataPacket {

	@Override
	public Map<Object, PojoAction> process(long currentDataProcessingRound, long currentRoutingRound, AodvNode aodvNode, List<Neighbour> allKnownNeighbours, List<AodvRoutingTableEntries> routingTable, Setting gameSettings) {
		return Maps.empty();
	}

	@Override
	public void setOnHoldUntil(long dataProcessingRound) {
	}

	@Override
	public Map<Object, PojoAction> save() {
		return Maps.empty();
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
