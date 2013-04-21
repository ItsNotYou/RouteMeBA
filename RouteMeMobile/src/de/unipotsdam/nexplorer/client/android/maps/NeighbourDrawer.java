package de.unipotsdam.nexplorer.client.android.maps;

import java.util.Map;

import de.unipotsdam.nexplorer.client.android.rest.Neighbour;

public interface NeighbourDrawer {

	public void removeInvisible(Map<Integer, Neighbour> neighbours);

	public void draw(Map<Integer, Neighbour> neighbours);
}
