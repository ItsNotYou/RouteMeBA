package de.unipotsdam.nexplorer.server.persistence;

import java.util.Comparator;

import de.unipotsdam.nexplorer.server.aodv.Locator;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.shared.Locatable;

public class ItemDistanceComparator implements Comparator<Items> {

	private Locatable location;
	private Locator locator;

	public ItemDistanceComparator(Locatable location, Locator locator) {
		this.location = location;
		this.locator = locator;
	}

	@Override
	public int compare(Items o1, Items o2) {
		double d1 = locator.distance(location, o1);
		double d2 = locator.distance(location, o2);

		double delta = d1 - d2;
		return Math.round(new Float(delta));
	}
}
