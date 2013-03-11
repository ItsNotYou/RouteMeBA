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

	public MapRotator(FragmentActivity host, int mapId) {
		this.host = host;
		this.mapId = mapId;
		setUpMapIfNeeded();
	}

	public void setUpMapIfNeeded() {
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

					if (mMap != null) {
						CameraPosition pos = new CameraPosition.Builder().target(new LatLng(37.4, -122.1)) // Sets the center of the map to Mountain View
								.zoom(25) // Sets the zoom
								.bearing(bearing) // Sets the orientation of the camera to east
								.tilt(newAngle) // Sets the tilt of the camera to 30 degrees
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

		SensorManager sensorManager = (SensorManager) host.getSystemService(MapActivity.SENSOR_SERVICE);
		sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);

		mMap = ((SupportMapFragment) host.getSupportFragmentManager().findFragmentById(mapId)).getMap();
	}
}