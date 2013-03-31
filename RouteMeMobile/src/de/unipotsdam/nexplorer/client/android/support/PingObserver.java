package de.unipotsdam.nexplorer.client.android.support;

import de.unipotsdam.nexplorer.client.android.callbacks.Pingable;

public class PingObserver extends ObserverWithoutParameter<Pingable> {

	@Override
	protected void call(Pingable callback) {
		callback.pingRequested();
	}
}
