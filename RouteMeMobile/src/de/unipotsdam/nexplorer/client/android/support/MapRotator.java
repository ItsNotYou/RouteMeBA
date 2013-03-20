package de.unipotsdam.nexplorer.client.android.support;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import de.unipotsdam.nexplorer.client.android.MapActivity;

public class MapRotator {

	private GoogleMap mMap = null;
	private FragmentActivity host;
	private int mapId;
	private LatLng currentLocation = null;

	public MapRotator(FragmentActivity host, int mapId) {
		this.host = host;
		this.mapId = mapId;
	}

	public void setCurrentLocation(LatLng location) {
		this.currentLocation = location;
	}

	public void setUpMapIfNeeded(boolean in3d) {
		SensorEventListener listener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				// Handle the events for which we registered
				switch (event.sensor.getType()) {
				case Sensor.TYPE_ORIENTATION:
					float angle = event.values[1];
					float newAngle = -angle;
					newAngle = Math.min(90, newAngle);
					newAngle = Math.max(0, newAngle);

					float bearing = event.values[0];

					if (mMap != null && currentLocation != null) {
						CameraPosition pos = new CameraPosition.Builder().target(currentLocation).bearing(bearing) // Sets the orientation of the camera to east
								.zoom(19).tilt(newAngle) // Sets the tilt of the camera to 30 degrees
								.build(); // Creates a CameraPosition from the builder
						mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
					}

					break;
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};

		if (in3d) {
			SensorManager sensorManager = (SensorManager) host.getSystemService(MapActivity.SENSOR_SERVICE);
			sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
		}

		mMap = ((SupportMapFragment) host.getSupportFragmentManager().findFragmentById(mapId)).getMap();
		mMap.setMyLocationEnabled(false);
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		mMap.moveCamera(CameraUpdateFactory.zoomTo(19));

		// useCurrentLocation : false,
		// mapOptions : {
		// zoom : 19,
		// mapTypeId : google.maps.MapTypeId.ROADMAP,
		// navigationControl : false,
		// disableDefaultUI : true,
		// disableDoubleClickZoom : true,
		// draggable : false,
		// scrollwheel : false
		// }
	}

	public GoogleMap getMap() {
		return mMap;
	}
}