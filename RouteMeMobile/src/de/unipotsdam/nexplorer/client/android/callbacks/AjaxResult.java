package de.unipotsdam.nexplorer.client.android.callbacks;

public class AjaxResult<T> {

	public void success() {
	}

	public void success(T result) {
		success();
	}

	public void error() {
	}

	public void error(Exception e) {
		error();
	}
}
