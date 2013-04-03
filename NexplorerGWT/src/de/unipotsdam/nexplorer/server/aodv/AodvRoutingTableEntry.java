package de.unipotsdam.nexplorer.server.aodv;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingTableEntries;

public class AodvRoutingTableEntry {

	private final AodvRoutingTableEntries inner;
	private final DatabaseImpl dbAccess;
	private final AodvFactory factory;

	@Inject
	public AodvRoutingTableEntry(@Assisted AodvRoutingTableEntries inner, DatabaseImpl dbAccess, AodvFactory factory) {
		this.inner = inner;
		this.dbAccess = dbAccess;
		this.factory = factory;
	}

	public AodvNode getDestination() {
		Player player = dbAccess.getPlayerById(inner.getDestinationId());
		return factory.create(player);
	}

	public AodvNode getNextHop() {
		return factory.create(dbAccess.getPlayerById(inner.getNextHopId()));
	}

	public long getDestinationSequenceNumber() {
		return inner.getDestinationSequenceNumber();
	}

	public long getHopCount() {
		return inner.getHopCount();
	}

	public void delete() {
		dbAccess.delete(inner);
	}
}
