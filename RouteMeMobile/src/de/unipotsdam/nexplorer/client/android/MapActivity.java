package de.unipotsdam.nexplorer.client.android;

import java.util.Date;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends FragmentActivity {

	GoogleMap mMap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		setUpMapIfNeeded();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_map, menu);
		return true;
	}

	private void setUpMapIfNeeded() {
		SensorEventListener listener = new SensorEventListener() {

			private Date last = new Date();

			@Override
			public void onSensorChanged(SensorEvent event) {
				// Handle the events for which we registered
				switch (event.sensor.getType()) {
				case Sensor.TYPE_ORIENTATION:
					Date now = new Date();
					float angle = event.values[1];
					float newAngle = -angle;
					newAngle = Math.min(90, newAngle);
					newAngle = Math.max(0, newAngle);

					float bearing = event.values[0];
					if (now.getTime() - last.getTime() > 5000) {
						Toast.makeText(MapActivity.this, "Changed to " + newAngle + " degrees", Toast.LENGTH_SHORT).show();
						last = now;
					}

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

		SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);

		mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	}
}
