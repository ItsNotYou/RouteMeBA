package de.unipotsdam.nexplorer.server.data;

import java.util.List;

import de.unipotsdam.nexplorer.server.persistence.PlayerInternal;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class SetNearbyItems implements PlayerInternal {

	private List<Items> items;

	public SetNearbyItems(List<Items> items) {
		this.items = items;
	}

	@Override
	public void execute(Players inner) {
		inner.setNearbyItems(items);
	}
}
