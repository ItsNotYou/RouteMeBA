package de.unipotsdam.nexplorer.client.android.support;

import de.unipotsdam.nexplorer.client.android.callbacks.Loginable;

public class LoginObserver extends ObserverWithParameter<Loginable, Long> {

	@Override
	protected void call(Loginable callback, Long parameter) {
		callback.loggedIn(parameter);
	}
}
