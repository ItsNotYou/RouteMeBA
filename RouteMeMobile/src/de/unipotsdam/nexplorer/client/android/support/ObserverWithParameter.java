package de.unipotsdam.nexplorer.client.android.support;

public abstract class ObserverWithParameter<T, U> extends AbstractObserver<T> {

	public void fire(U parameter) {
		for (T callback : getCallbacks()) {
			call(callback, parameter);
		}
	}

	protected abstract void call(T callback, U parameter);
}
