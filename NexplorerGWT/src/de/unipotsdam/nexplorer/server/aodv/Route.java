package de.unipotsdam.nexplorer.server.aodv;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingTableEntries;

public class Route {

	private final long nextHop;
	private final long dest;
	private final long sequenceNumber;
	private final long hopCount;

	public Route(long nextHop, long dest, long sequenceNumber, long hopCount) {
		this.nextHop = nextHop;
		this.dest = dest;
		this.sequenceNumber = sequenceNumber;
		this.hopCount = hopCount;
	}

	public long getNextHop() {
		return nextHop;
	}

	public long getDest() {
		return dest;
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public long getHopCount() {
		return hopCount;
	}

	Collection<Object> persist(long src) {
		AodvRoutingTableEntries entry = new AodvRoutingTableEntries();
		entry.setDestinationId(dest);
		entry.setDestinationSequenceNumber(sequenceNumber);
		entry.setHopCount(hopCount);
		entry.setNextHopId(nextHop);
		entry.setNodeId(src);
		entry.setTimestamp(new Date().getTime());
		return Arrays.asList((Object) entry);
	}
}
