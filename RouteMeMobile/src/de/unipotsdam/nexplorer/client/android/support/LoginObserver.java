package de.unipotsdam.nexplorer.client.android.support;

import de.unipotsdam.nexplorer.client.android.callbacks.Loginable;

public class LoginObserver extends ObserverWithParameter<Loginable, Integer> {

	@Override
	protected void call(Loginable callback, Integer parameter) {
		callback.loggedIn(parameter);
	}
}
