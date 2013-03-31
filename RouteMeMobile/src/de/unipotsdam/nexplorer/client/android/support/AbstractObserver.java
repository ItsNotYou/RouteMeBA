package de.unipotsdam.nexplorer.client.android.support;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractObserver<T, U> {

	private final Collection<T> callbacks;

	protected AbstractObserver() {
		this.callbacks = new ArrayList<T>();
	}

	public void add(T callback) {
		callbacks.add(callback);
	}

	public void fire(U parameter) {
		for (T callback : callbacks) {
			call(callback, parameter);
		}
	}

	protected abstract void call(T callback, U parameter);
}
