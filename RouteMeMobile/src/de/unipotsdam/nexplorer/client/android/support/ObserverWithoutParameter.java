package de.unipotsdam.nexplorer.client.android.support;

public abstract class ObserverWithoutParameter<T> extends AbstractObserver<T> {

	public void fire() {
		for (T callback : getCallbacks()) {
			call(callback);
		}
	}

	protected abstract void call(T callback);
}
