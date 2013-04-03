package de.unipotsdam.nexplorer.server.persistence;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Neighbours;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class AsNeighbour implements PlayerInternal {

	private Neighbours neigh;

	public AsNeighbour(Neighbours neigh) {
		this.neigh = neigh;
	}

	@Override
	public void execute(Players inner) {
		neigh.setNeighbour(inner);
	}
}
