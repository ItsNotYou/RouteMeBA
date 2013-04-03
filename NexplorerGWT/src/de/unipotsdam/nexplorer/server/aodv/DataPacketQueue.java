package de.unipotsdam.nexplorer.server.aodv;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.unipotsdam.nexplorer.server.persistence.ProcessableDataPacket;

public class DataPacketQueue extends AbstractQueue<ProcessableDataPacket> {

	private final LinkedList<ProcessableDataPacket> inner;

	public DataPacketQueue() {
		super();
		this.inner = new LinkedList<ProcessableDataPacket>();
	}

	public DataPacketQueue(List<? extends ProcessableDataPacket> packets) {
		this();
		addAll(packets);
	}

	@Override
	public boolean offer(ProcessableDataPacket e) {
		inner.addLast(e);
		return true;
	}

	public void placeContentOnHoldUntil(long dataProcessingRound) {
		for (ProcessableDataPacket packet : this) {
			packet.setOnHoldUntil(dataProcessingRound);
			packet.save();
		}
	}

	@Override
	public ProcessableDataPacket poll() {
		return inner.isEmpty() ? new NullPacket() : inner.remove(0);
	}

	@Override
	public ProcessableDataPacket peek() {
		return inner.isEmpty() ? new NullPacket() : inner.get(0);
	}

	@Override
	public Iterator<ProcessableDataPacket> iterator() {
		return inner.iterator();
	}

	@Override
	public int size() {
		return inner.size();
	}
}
