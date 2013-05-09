package de.unipotsdam.nexplorer.client.android.js;

import de.unipotsdam.nexplorer.client.android.callbacks.Collectable;
import de.unipotsdam.nexplorer.client.android.sensors.TouchVibrator;

public class CollectItemVibration implements Collectable {

	private TouchVibrator vibrator;

	public CollectItemVibration(TouchVibrator vibrator) {
		this.vibrator = vibrator;
	}

	@Override
	public void collectRequested(Integer itemId) {
		vibrator.vibrate();
	}
}
