package de.unipotsdam.nexplorer.server.aodv;

import de.unipotsdam.nexplorer.server.persistence.Item;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.shared.Locatable;
import de.unipotsdam.nexplorer.shared.Location;

public class Locator {

	public boolean isInRange(Player src, Player dest) {
		return calculateDistance(src, dest) <= src.getRange() / 1000.;
	}

	// Entfernung zu anderem Knoten in km
	private double calculateDistance(Locatable src, Locatable dest) {
		double R = 6371; // Erdradius in km

		double lat1 = Math.toRadians(src.getLatitude());
		double lat2 = Math.toRadians(dest.getLatitude());
		double lon1 = Math.toRadians(src.getLongitude());
		double lon2 = Math.toRadians(dest.getLongitude());
		if (lat2 == 0 && lon2 == 0) {
			return Double.MAX_VALUE;
		}

		double a = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1));
		double d = R * a;

		return d;
	}

	public double distance(Locatable from, Locatable to) {
		return calculateDistance(from, to);
	}

	public double distance(Player player, Item collectible) {
		return calculateDistance(player, collectible);
	}

	public double distance(Locatable location, Player player) {
		return calculateDistance(location, player);
	}

	public double distance(Locatable from, Items to) {
		Location toLocation = new Location();
		toLocation.setLongitude(to.getLongitude());
		toLocation.setLatitude(to.getLatitude());

		return calculateDistance(from, toLocation);
	}
}
