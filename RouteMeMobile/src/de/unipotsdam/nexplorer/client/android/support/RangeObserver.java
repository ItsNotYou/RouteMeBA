package de.unipotsdam.nexplorer.client.android.support;

import de.unipotsdam.nexplorer.client.android.callbacks.Rangeable;

public class RangeObserver extends ObserverWithParameter<Rangeable, Double> {

	@Override
	protected void call(Rangeable callback, Double parameter) {
		callback.rangeChanged(parameter);
	}
}
