package de.unipotsdam.nexplorer.client.android.support;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractObserver<T> {

	private final Collection<T> callbacks;

	protected AbstractObserver() {
		this.callbacks = new ArrayList<T>();
	}

	public void add(T callback) {
		callbacks.add(callback);
	}

	protected Iterable<T> getCallbacks() {
		return this.callbacks;
	}
}
