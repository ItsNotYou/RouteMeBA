package de.unipotsdam.nexplorer.client.android.maps;

import static de.unipotsdam.nexplorer.client.android.js.Window.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import de.unipotsdam.nexplorer.client.android.R.drawable;
import de.unipotsdam.nexplorer.client.android.js.LatLng;
import de.unipotsdam.nexplorer.client.android.js.Marker;
import de.unipotsdam.nexplorer.client.android.js.MarkerImage;
import de.unipotsdam.nexplorer.client.android.rest.Neighbour;

public class LevelOneNeighbourDrawer implements NeighbourDrawer {

	private java.util.Map<Integer, Marker> neighbourMarkersArray = new HashMap<Integer, Marker>();
	private de.unipotsdam.nexplorer.client.android.js.Map senchaMap;

	public LevelOneNeighbourDrawer(de.unipotsdam.nexplorer.client.android.js.Map senchaMap, Activity host) {
		this.senchaMap = senchaMap;
	}

	/**
	 * draw the neighbours
	 * 
	 * @param playerId
	 * @param latitude
	 * @param longitude
	 */
	protected void drawNeighbourMarkerAtLatitudeLongitude(final int playerId, Neighbour neighbour) {
		final LatLng latlng = new LatLng(neighbour.getLatitude(), neighbour.getLongitude());
		final MarkerImage image = new MarkerImage(drawable.network_wireless_small);

		if (neighbourMarkersArray.get(playerId) == null) {
			Marker marker = new Marker(ui) {

				protected void setData() {
					position = latlng;
					map = senchaMap;
					title = "(" + playerId + ") ";
					icon = image;
					zIndex = 1;
				}
			};

			neighbourMarkersArray.put(playerId, marker);
		} else {
			neighbourMarkersArray.get(playerId).setPosition(latlng);
			neighbourMarkersArray.get(playerId).setTitle("(" + playerId + ") " /* + name */);
			if (neighbourMarkersArray.get(playerId).map == null) {
				neighbourMarkersArray.get(playerId).setMap(senchaMap);
			}
		}
	}

	@Override
	public void removeInvisible(Map<Integer, Neighbour> neighbours) {
		for (Map.Entry<Integer, Marker> entry : neighbourMarkersArray.entrySet()) {
			if (entry.getValue() != null && neighbours.get(entry.getKey()) == null) {
				remove(entry.getKey(), entry.getValue());
			}
		}
	}

	protected void remove(int playerId, Marker neighbour) {
		neighbour.setMap(null);
	}

	@Override
	public void draw(Map<Integer, Neighbour> neighbours) {
		for (Map.Entry<Integer, Neighbour> entry : neighbours.entrySet()) {
			drawNeighbourMarkerAtLatitudeLongitude(entry.getKey(), entry.getValue());
		}
	}
}
