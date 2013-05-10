package de.unipotsdam.nexplorer.client.android.js;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import de.unipotsdam.nexplorer.client.android.R.drawable;
import de.unipotsdam.nexplorer.client.android.maps.LevelOneNeighbourDrawer;
import de.unipotsdam.nexplorer.client.android.maps.LevelTwoNeighbourDrawer;
import de.unipotsdam.nexplorer.client.android.maps.NeighbourDrawer;
import de.unipotsdam.nexplorer.client.android.rest.Item;
import de.unipotsdam.nexplorer.client.android.rest.Neighbour;

public class MapRelatedTasks {

	private final de.unipotsdam.nexplorer.client.android.js.Map senchaMap;
	private final Activity host;
	private java.util.Map<Integer, Marker> nearbyItemMarkersArray = new HashMap<Integer, Marker>();
	private NeighbourDrawer neighbourDrawer;

	private Marker playerMarker;
	private PlayerRadius playerRadius;
	private PlayerRadius collectionRadius;

	private Location oldLocation;
	private Integer oldPlayerRange;
	private Integer oldItemRange;

	public MapRelatedTasks(de.unipotsdam.nexplorer.client.android.js.Map senchaMap, Activity host, Marker playerMarker, PlayerRadius playerRadius, PlayerRadius collectionRadius) {
		this.senchaMap = senchaMap;
		this.host = host;
		this.playerMarker = playerMarker;
		this.playerRadius = playerRadius;
		this.collectionRadius = collectionRadius;

		this.oldLocation = null;
		this.oldPlayerRange = null;
		this.oldItemRange = null;
		this.neighbourDrawer = null;
	}

	void drawMarkers(Map<Integer, Neighbour> neighbours, Map<Integer, Item> nearbyItems, String difficulty) {
		ensureNeighbourDrawer(difficulty);

		if (neighbours != null && neighbourDrawer != null) {
			neighbourDrawer.draw(neighbours);
		}

		if (nearbyItems != null) {
			for (Map.Entry<Integer, Item> entry : nearbyItems.entrySet()) {
				drawNearbyItemMarkerAtLatitudeLongitude(entry.getKey(), entry.getValue().getItemType(), entry.getValue().getLatitude(), entry.getValue().getLongitude());
			}
		}
	}

	private void ensureNeighbourDrawer(String difficulty) {
		if (neighbourDrawer != null) {
			return;
		}

		if (difficulty == null) {
			return;
		}

		if (difficulty.equals("1")) {
			neighbourDrawer = new LevelOneNeighbourDrawer(senchaMap, host);
		} else if (difficulty.equals("2")) {
			neighbourDrawer = new LevelTwoNeighbourDrawer(senchaMap, host);
		}
	}

	/**
	 * draw nearby items
	 * 
	 * @param itemId
	 * @param type
	 * @param latitude
	 * @param longitude
	 */
	void drawNearbyItemMarkerAtLatitudeLongitude(int itemId, String type, double latitude, double longitude) {
		final LatLng latlng = new LatLng(latitude, longitude);

		int imagePath = 0;
		if ("BATTERY".equals(type)) {
			imagePath = drawable.battery_charge;
		} else {
			imagePath = drawable.mobile_phone_cast;
		}

		final MarkerImage image = new MarkerImage(imagePath);

		if (nearbyItemMarkersArray.get(itemId) == null) {
			Marker marker = new Marker(host) {

				protected void setData() {
					position = latlng;
					map = senchaMap;
					icon = image;
					zIndex = 1;
				}
			};

			nearbyItemMarkersArray.put(itemId, marker);
		} else {
			nearbyItemMarkersArray.get(itemId).setPosition(latlng);
			if (nearbyItemMarkersArray.get(itemId).map == null) {
				nearbyItemMarkersArray.get(itemId).setMap(senchaMap);
			}
		}
	}

	void removeInvisibleMarkers(final java.util.Map<Integer, Neighbour> neighbours, final java.util.Map<Integer, Item> nearbyItems, String difficulty) {
		ensureNeighbourDrawer(difficulty);

		if (neighbours != null && neighbourDrawer != null) {
			neighbourDrawer.removeInvisible(neighbours);
		}

		for (Map.Entry<Integer, Marker> entry : nearbyItemMarkersArray.entrySet()) {
			if (entry.getValue() != null && nearbyItems.get(entry.getKey()) == null) {
				nearbyItemMarkersArray.get(entry.getKey()).setMap(null);
			}
		}
	}

	void centerAtCurrentLocation(final Location currentLocation, final Integer playerRange, final Integer itemCollectionRange) {
		if (currentLocation == oldLocation && playerRange.equals(oldPlayerRange) && itemCollectionRange.equals(oldItemRange)) {
			return;
		}

		this.oldLocation = currentLocation;
		this.oldPlayerRange = playerRange;
		this.oldItemRange = itemCollectionRange;

		host.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (currentLocation != null) {
					// Karte zentrieren
					senchaMap.setCenter(new LatLng(currentLocation));
					// Spieler Marker zentrieren
					playerMarker.setPosition(new LatLng(currentLocation));
					if (playerMarker.map == null) {
						playerMarker.setMap(senchaMap);
					}
					// Senderadius zentrieren
					playerRadius.setCenter(new LatLng(currentLocation));
					if (playerRadius.map == null) {
						playerRadius.setMap(senchaMap);
					}
					playerRadius.setRadius(playerRange);
					// Sammelradius zentrieren
					collectionRadius.setCenter(new LatLng(currentLocation));
					if (collectionRadius.map == null) {
						collectionRadius.setMap(senchaMap);
					}
					collectionRadius.setRadius(itemCollectionRange);
				}
			}
		});
	}

	public void setOnMapClickListener(final OnMapClickListener listener) {
		this.senchaMap.getMap().setOnMapClickListener(listener);
		this.senchaMap.getMap().setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
				listener.onMapClick(marker.getPosition());
				return true;
			}
		});
	}
}
