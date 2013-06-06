package de.unipotsdam.nexplorer.client.android;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import de.unipotsdam.nexplorer.client.android.R.drawable;
import de.unipotsdam.nexplorer.client.android.callbacks.Locatable;
import de.unipotsdam.nexplorer.client.android.js.LatLng;
import de.unipotsdam.nexplorer.client.android.js.Marker;
import de.unipotsdam.nexplorer.client.android.js.MarkerImage;
import de.unipotsdam.nexplorer.client.android.js.PlayerRadius;
import de.unipotsdam.nexplorer.client.android.maps.LevelOneNeighbourDrawer;
import de.unipotsdam.nexplorer.client.android.maps.LevelTwoNeighbourDrawer;
import de.unipotsdam.nexplorer.client.android.maps.NeighbourDrawer;
import de.unipotsdam.nexplorer.client.android.rest.Item;
import de.unipotsdam.nexplorer.client.android.rest.Neighbour;

public class NexplorerMap extends RotatingMapFragment implements Locatable {

	private java.util.Map<Integer, Marker> nearbyItemMarkersArray = new HashMap<Integer, Marker>();
	private NeighbourDrawer neighbourDrawer;

	private Marker playerMarker;
	private PlayerRadius playerRadius;
	private PlayerRadius collectionRadius;

	private Location oldLocation;
	private Integer oldPlayerRange;
	private Integer oldItemRange;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.oldLocation = null;
		this.oldPlayerRange = null;
		this.oldItemRange = null;
		this.neighbourDrawer = null;

		playerMarker = new Marker(getActivity()) {

			protected void setData() {
				MarkerImage image = new MarkerImage(R.drawable.home_network);
				this.icon = image;
			};
		};

		int strokeColor = Color.parseColor("#5A0000FF");
		int strokeWeight = 2;
		int fillColor = Color.parseColor("#330000FF");
		playerRadius = new PlayerRadius(getActivity(), strokeColor, strokeWeight, fillColor);
		strokeColor = Color.parseColor("#5AFF0000");
		strokeWeight = 1;
		fillColor = Color.parseColor("#40FF0000");
		collectionRadius = new PlayerRadius(getActivity(), strokeColor, strokeWeight, fillColor);
	}

	private void drawMarkers(Map<Integer, Neighbour> neighbours, Map<Integer, Item> nearbyItems, String difficulty) {
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
			neighbourDrawer = new LevelOneNeighbourDrawer(googleMap, getActivity());
		} else if (difficulty.equals("2")) {
			neighbourDrawer = new LevelTwoNeighbourDrawer(googleMap, getActivity());
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
	private void drawNearbyItemMarkerAtLatitudeLongitude(int itemId, String type, double latitude, double longitude) {
		final LatLng latlng = new LatLng(latitude, longitude);

		int imagePath = 0;
		if ("BATTERY".equals(type)) {
			imagePath = drawable.battery_charge;
		} else {
			imagePath = drawable.mobile_phone_cast;
		}

		final MarkerImage image = new MarkerImage(imagePath);

		if (nearbyItemMarkersArray.get(itemId) == null) {
			Marker marker = new Marker(getActivity()) {

				protected void setData() {
					position = latlng;
					map = googleMap;
					icon = image;
					zIndex = 1;
				}
			};

			nearbyItemMarkersArray.put(itemId, marker);
		} else {
			nearbyItemMarkersArray.get(itemId).setPosition(latlng);
			if (nearbyItemMarkersArray.get(itemId).map == null) {
				nearbyItemMarkersArray.get(itemId).setMap(googleMap);
			}
		}
	}

	public void removeInvisibleMarkers(final java.util.Map<Integer, Neighbour> neighbours, final java.util.Map<Integer, Item> nearbyItems, String difficulty) {
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

	private void setCenter(final LatLng latLng) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (map != null) {
					map.setCurrentLocation(latLng.create());
				} else {
					CameraUpdate update = CameraUpdateFactory.newLatLng(latLng.create());
					googleMap.moveCamera(update);
				}
			}
		});
	}

	private void updateMarkerSizes(final Integer playerRange, final Integer itemCollectionRange) {
		if (playerRange.equals(oldPlayerRange) && itemCollectionRange.equals(oldItemRange)) {
			return;
		}

		this.oldPlayerRange = playerRange;
		this.oldItemRange = itemCollectionRange;

		playerRadius.setRadius(playerRange);
		collectionRadius.setRadius(itemCollectionRange);
	}

	public void centerAt(final Location currentLocation) {
		if (currentLocation == oldLocation) {
			return;
		}

		this.oldLocation = currentLocation;
		if (currentLocation != null) {
			// Karte zentrieren
			setCenter(new LatLng(currentLocation));
			// Spieler Marker zentrieren
			playerMarker.setPosition(new LatLng(currentLocation));
			if (playerMarker.map == null) {
				playerMarker.setMap(googleMap);
			}
			// Senderadius zentrieren
			playerRadius.setCenter(new LatLng(currentLocation));
			if (playerRadius.map == null) {
				playerRadius.setMap(googleMap);
			}
			// Sammelradius zentrieren
			collectionRadius.setCenter(new LatLng(currentLocation));
			if (collectionRadius.map == null) {
				collectionRadius.setMap(googleMap);
			}
		}
	}

	public void setOnMapClickListener(final OnMapClickListener listener) {
		googleMap.setOnMapClickListener(listener);
		googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
				listener.onMapClick(marker.getPosition());
				return true;
			}
		});
	}

	public void updateMap(int playerRange, int itemCollectionRange, Map<Integer, Neighbour> neighbours, Map<Integer, Item> nearbyItems, String gameDifficulty) {
		updateMarkerSizes(playerRange, itemCollectionRange);
		drawMarkers(neighbours, nearbyItems, gameDifficulty);
	}

	@Override
	public void locationChanged(Location location) {
		centerAt(location);
	}
}
