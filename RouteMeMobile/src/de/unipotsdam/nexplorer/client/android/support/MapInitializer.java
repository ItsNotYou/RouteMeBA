package de.unipotsdam.nexplorer.client.android.support;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MapInitializer {

	private FragmentActivity host;
	private int mapId;

	public MapInitializer(FragmentActivity host, int mapId) {
		this.host = host;
		this.mapId = mapId;
	}

	public GoogleMap initMap() {
		GoogleMap mMap = ((SupportMapFragment) host.getSupportFragmentManager().findFragmentById(mapId)).getMap();
		mMap.setMyLocationEnabled(false);
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.moveCamera(CameraUpdateFactory.zoomTo(19));

		return mMap;
	}
}
