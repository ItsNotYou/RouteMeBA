package de.unipotsdam.nexplorer.server.persistence;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Neighbours;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public interface DataFactory {

	Player create(Players inner);

	Neighbour create(Neighbours inner);

	NeighbourSet neighbours(Players inner);
	
	Item create(Items inner);
}
