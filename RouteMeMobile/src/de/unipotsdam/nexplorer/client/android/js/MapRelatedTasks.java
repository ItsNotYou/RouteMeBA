package de.unipotsdam.nexplorer.client.android.js;

import static de.unipotsdam.nexplorer.client.android.js.Window.collectionRadius;
import static de.unipotsdam.nexplorer.client.android.js.Window.each;
import static de.unipotsdam.nexplorer.client.android.js.Window.playerMarker;
import static de.unipotsdam.nexplorer.client.android.js.Window.playerRadius;
import static de.unipotsdam.nexplorer.client.android.js.Window.senchaMap;
import static de.unipotsdam.nexplorer.client.android.js.Window.ui;
import static de.unipotsdam.nexplorer.client.android.js.Window.undefined;

import java.util.HashMap;
import java.util.Map;

import de.unipotsdam.nexplorer.client.android.R.drawable;
import de.unipotsdam.nexplorer.client.android.support.Location;

public class MapRelatedTasks {

	private java.util.Map<Integer, Marker> nearbyItemMarkersArray = new HashMap<Integer, Marker>();
	private java.util.Map<Integer, Marker> neighbourMarkersArray = new HashMap<Integer, Marker>();

	void drawMarkers(Map<Integer, Neighbour> neighbours, Map<Integer, Item> nearbyItems) {
		if (neighbours != undefined) {
			each(neighbours, new Call<Integer, Neighbour>() {

				@Override
				public void call(Integer key, Neighbour value) {
					drawNeighbourMarkerAtLatitudeLongitude(key, value.getLatitude(), value.getLongitude());
				}
			});
		}

		if (nearbyItems != undefined) {
			each(nearbyItems, new Call<Integer, Item>() {

				@Override
				public void call(Integer key, Item value) {
					drawNearbyItemMarkerAtLatitudeLongitude(key, value.getItemType(), value.getLatitude(), value.getLongitude());
				}
			});
		}
	}

	/**
	 * draw nearby items
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

		final MarkerImage image = new MarkerImage(imagePath, new Size(16, 16),
		// The origin for this image is 0,0.
				new Point(0, 0),
				// The anchor for this image is the base of the flagpole at 0,32.
				new Point(8, 8));

		if (nearbyItemMarkersArray.get(itemId) == undefined) {
			Marker marker = new Marker(ui) {

				protected void setData() {
					position = latlng;
					map = senchaMap.map;
					icon = image;
					zIndex = 1;
				}
			};

			nearbyItemMarkersArray.put(itemId, marker);
		} else {
			nearbyItemMarkersArray.get(itemId).setPosition(latlng);
			if (nearbyItemMarkersArray.get(itemId).map == null) {
				nearbyItemMarkersArray.get(itemId).setMap(senchaMap.map);
			}
		}
	}

	void removeInvisibleMarkers(final java.util.Map<Integer, Neighbour> neighbours, final java.util.Map<Integer, Item> nearbyItems) {
		each(neighbourMarkersArray, new Call<Integer, Marker>() {

			public void call(Integer key, Marker theMarker) {
				if (theMarker != undefined && neighbours.get(key) == undefined) {
					neighbourMarkersArray.get(key).setMap(null);
				}
			}
		});

		each(nearbyItemMarkersArray, new Call<Integer, Marker>() {

			public void call(Integer key, Marker theMarker) {
				if (theMarker != undefined && nearbyItems.get(key) == undefined) {
					nearbyItemMarkersArray.get(key).setMap(null);
				}
			}
		});
	}

	/**
	 * draw the neighbours
	 * 
	 * @param playerId
	 * @param latitude
	 * @param longitude
	 */
	void drawNeighbourMarkerAtLatitudeLongitude(final int playerId, double latitude, double longitude) {
		final LatLng latlng = new LatLng(latitude, longitude);

		final MarkerImage image = new MarkerImage(drawable.network_wireless_small, new Size(16, 16),
		// The origin for this image is 0,0.
				new Point(0, 0),
				// The anchor for this image is the base of the flagpole at 0,32.
				new Point(8, 8));

		if (neighbourMarkersArray.get(playerId) == undefined) {
			Marker marker = new Marker(ui) {

				protected void setData() {
					position = latlng;
					map = senchaMap.map;
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
				neighbourMarkersArray.get(playerId).setMap(senchaMap.map);
			}
		}
	}

	void centerAtCurrentLocation(Location currentLocation, int playerRange, int itemCollectionRange) {
		if (currentLocation != null) {
			// Karte zentrieren
			senchaMap.map.setCenter(new LatLng(currentLocation));
			// Spieler Marker zentrieren
			playerMarker.setPosition(new LatLng(currentLocation));
			if (playerMarker.map == null) {
				playerMarker.setMap(senchaMap.map);
			}
			// Senderadius zentrieren
			playerRadius.setCenter(new LatLng(currentLocation));
			if (playerRadius.map == null) {
				playerRadius.setMap(senchaMap.map);
			}
			playerRadius.setRadius(playerRange);
			// Sammelradius zentrieren
			collectionRadius.setCenter(new LatLng(currentLocation));
			if (collectionRadius.map == null) {
				collectionRadius.setMap(senchaMap.map);
			}
			collectionRadius.setRadius(itemCollectionRange);
		}
	}
}
