package de.unipotsdam.nexplorer.server.aodv;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingMessages;

public class AodvRoutingMessage {

	private final AodvRoutingMessages inner;
	private final DatabaseImpl dbAccess;

	@Inject
	public AodvRoutingMessage(@Assisted AodvRoutingMessages inner, DatabaseImpl dbAccess) {
		this.inner = inner;
		this.dbAccess = dbAccess;
	}

	public AodvRoutingMessages inner() {
		return this.inner;
	}

	public void delete() {
		dbAccess.delete(inner);
	}

	public boolean isExpired() {
		return inner.getLifespan() <= 0;
	}
}
