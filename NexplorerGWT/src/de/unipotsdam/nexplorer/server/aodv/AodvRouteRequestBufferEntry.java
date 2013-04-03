package de.unipotsdam.nexplorer.server.aodv;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRouteRequestBufferEntries;

public class AodvRouteRequestBufferEntry {

	private AodvRouteRequestBufferEntries inner;

	@Inject
	public AodvRouteRequestBufferEntry(@Assisted AodvRouteRequestBufferEntries inner) {
		this.inner = inner;
	}

	public AodvRouteRequestBufferEntries inner() {
		return inner;
	}
}
