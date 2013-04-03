package de.unipotsdam.nexplorer.server.persistence;

import java.util.Comparator;

import de.unipotsdam.nexplorer.server.aodv.Locator;
import de.unipotsdam.nexplorer.shared.Locatable;

public class DistanceComparator implements Comparator<Player> {

	private Locatable location;
	private Locator locator;

	public DistanceComparator(Locatable location, Locator locator) {
		this.location = location;
		this.locator = locator;
	}

	@Override
	public int compare(Player o1, Player o2) {
		double d1 = locator.distance(location, o1);
		double d2 = locator.distance(location, o2);

		double delta = d1 - d2;
		return Math.round(new Float(delta));
	}
}
