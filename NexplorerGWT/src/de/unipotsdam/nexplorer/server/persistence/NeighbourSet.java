package de.unipotsdam.nexplorer.server.persistence;

import java.util.AbstractSet;
import java.util.Iterator;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.unipotsdam.nexplorer.server.data.PlayerDoesNotExistException;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Neighbours;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class NeighbourSet extends AbstractSet<Player> {

	private Players inner;
	private DatabaseImpl dbAccess;
	private DataFactory data;

	@Inject
	public NeighbourSet(@Assisted Players inner, DatabaseImpl dbAccess, DataFactory data) {
		this.inner = inner;
		this.dbAccess = dbAccess;
		this.data = data;
	}

	@Override
	public boolean add(Player e) {
		Neighbours neigh = new Neighbours();
		neigh.setNode(inner);
		try {
			e.execute(new AsNeighbour(neigh));
		} catch (PlayerDoesNotExistException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return inner.getNeighbourses().add(neigh);
	}

	@Override
	public int size() {
		return inner.getNeighbourses().size();
	}

	@Override
	public Iterator<Player> iterator() {
		return new PlayerIterator(inner.getNeighbourses().iterator());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj.getClass() != getClass())
			return false;
		NeighbourSet other = (NeighbourSet) obj;
		if (inner != other.inner)
			return inner.equals(other.inner);

		return true;
	}

	private class PlayerIterator implements Iterator<Player> {

		private Iterator<Neighbours> items;
		private Neighbours current;

		public PlayerIterator(Iterator<Neighbours> items) {
			this.items = items;
		}

		@Override
		public boolean hasNext() {
			return items.hasNext();
		}

		@Override
		public Player next() {
			current = items.next();
			Players next = current.getNeighbour();
			return data.create(next);
		}

		@Override
		public void remove() {
			items.remove();
			dbAccess.persist(inner);
			dbAccess.delete(current);
		}
	}
}
