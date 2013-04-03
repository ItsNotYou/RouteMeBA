package de.unipotsdam.nexplorer.server.persistence;

import org.hibernate.Session;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Neighbours;

public class Neighbour {

	private final Neighbours inner;
	private final DatabaseImpl dbAccess;
	private DataFactory data;

	@Inject
	public Neighbour(@Assisted Neighbours inner, DatabaseImpl dbAccess, DataFactory data) {
		this.inner = inner;
		this.dbAccess = dbAccess;
		this.data = data;
	}

	public void save(Session session) {
		session.save(inner);
	}

	public Player getNeighbour() {
		return data.create(inner.getNeighbour());
	}

	public Player getNode() {
		return data.create(inner.getNode());
	}

	public void delete() {
		dbAccess.delete(inner);
	}
}
