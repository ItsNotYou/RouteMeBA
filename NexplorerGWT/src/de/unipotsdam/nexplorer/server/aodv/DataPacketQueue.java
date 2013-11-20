package de.unipotsdam.nexplorer.server.aodv;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.unipotsdam.nexplorer.server.PojoAction;
import de.unipotsdam.nexplorer.server.data.Maps;
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

	public Map<Object, PojoAction> placeContentOnHoldUntil(long dataProcessingRound) {
		Map<Object, PojoAction> persistables = Maps.empty();

		for (ProcessableDataPacket packet : this) {
			packet.setOnHoldUntil(dataProcessingRound);
			persistables.putAll(packet.save());
		}

		return persistables;
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
