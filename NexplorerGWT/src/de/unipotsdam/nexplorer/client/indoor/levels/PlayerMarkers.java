package de.unipotsdam.nexplorer.client.indoor.levels;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;

public class PlayerMarkers extends JavaScriptObject {

	protected PlayerMarkers() {
	}

	public final native JsArrayInteger getKeys()/*-{
		var result = [];
		for (key in this) {
			var id = parseInt(key);
			result.push(id);
		}
		return result;
	}-*/;
}
