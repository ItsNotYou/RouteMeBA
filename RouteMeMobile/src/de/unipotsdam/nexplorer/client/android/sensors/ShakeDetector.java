package de.unipotsdam.nexplorer.client.android.sensors;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Mainly taken from <a href="http://stackoverflow.com/questions/2317428/android-i-want-to-shake-it">stackoverflow</a>
 * 
 * @author hgessner
 * 
 */
public class ShakeDetector implements SensorEventListener {

	/* put this into your activity class */
	private SensorManager mSensorManager;
	private float mAccel; // acceleration apart from gravity
	private float mAccelCurrent; // current acceleration including gravity
	private float mAccelLast; // last acceleration including gravity
	private long lastNotification;

	private final List<ShakeListener> observers;
	private float threshold;
	private long timeout;

	/**
	 * Do this in onCreate
	 * 
	 * @param host
	 *            Activity used for sensor manager retreival
	 * @param threshold
	 *            Minimum acceleration, that is necessary, to count a move as shaking. Value of 1.0 works quit well.
	 * @param timeout
	 *            Minimum time between two shake notifications. When a shake notification is published, the timeout must pass before another shake notification gets published.
	 */
	public ShakeDetector(Activity host, float threshold, long timeout) {
		this.observers = new LinkedList<ShakeListener>();
		this.threshold = threshold;
		this.timeout = timeout;
		this.lastNotification = 0;

		mSensorManager = (SensorManager) host.getSystemService(Context.SENSOR_SERVICE);
		mAccel = 0.00f;
		mAccelCurrent = SensorManager.GRAVITY_EARTH;
		mAccelLast = SensorManager.GRAVITY_EARTH;
	}

	/**
	 * Call in onResume to register resources.
	 */
	public void onResume() {
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	}

	/**
	 * Call in onPause to save resources.
	 */
	public void onPause() {
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent se) {
		float x = se.values[0];
		float y = se.values[1];
		float z = se.values[2];
		mAccelLast = mAccelCurrent;
		mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
		float delta = mAccelCurrent - mAccelLast;
		mAccel = mAccel * 0.9f + delta * 0.1f; // perform low-cut filter

		if (mAccel > threshold) {
			shakeDetected(mAccel);
		}
	}

	private void shakeDetected(float accel) {
		long now = new Date().getTime();
		long diff = now - lastNotification;
		if (diff > timeout) {
			lastNotification = now;
			for (ShakeListener listener : observers) {
				listener.shakeDetected(accel);
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void addShakeListener(ShakeListener listener) {
		observers.add(listener);
	}

	public interface ShakeListener {

		public void shakeDetected(float accel);
	}
}
