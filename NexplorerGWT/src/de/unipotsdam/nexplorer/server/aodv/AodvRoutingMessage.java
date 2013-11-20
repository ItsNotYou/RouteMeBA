package de.unipotsdam.nexplorer.server.aodv;

import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.unipotsdam.nexplorer.server.PojoAction;
import de.unipotsdam.nexplorer.server.data.Maps;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingMessages;

public class AodvRoutingMessage {

	private final AodvRoutingMessages inner;

	@Inject
	public AodvRoutingMessage(@Assisted AodvRoutingMessages inner) {
		this.inner = inner;
	}

	public AodvRoutingMessages inner() {
		return this.inner;
	}

	public Map<Object, PojoAction> delete() {
		return Maps.create(inner, PojoAction.DELETE);
	}

	public boolean isExpired() {
		return inner.getLifespan() <= 0;
	}
}
