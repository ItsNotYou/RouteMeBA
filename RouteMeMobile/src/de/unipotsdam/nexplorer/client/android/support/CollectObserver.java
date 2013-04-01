package de.unipotsdam.nexplorer.client.android.support;

import de.unipotsdam.nexplorer.client.android.callbacks.Collectable;

public class CollectObserver extends ObserverWithParameter<Collectable, Integer> {

	@Override
	protected void call(Collectable callback, Integer parameter) {
		callback.collectRequested(parameter);
	}
}
