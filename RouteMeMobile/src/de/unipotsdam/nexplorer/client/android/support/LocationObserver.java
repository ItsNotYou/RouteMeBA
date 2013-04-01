package de.unipotsdam.nexplorer.client.android.support;

import de.unipotsdam.nexplorer.client.android.callbacks.Locatable;
import de.unipotsdam.nexplorer.client.android.commons.Location;

public class LocationObserver extends ObserverWithParameter<Locatable, Location> {

	@Override
	protected void call(Locatable callback, Location parameter) {
		callback.locationChanged(parameter);
	}
}
