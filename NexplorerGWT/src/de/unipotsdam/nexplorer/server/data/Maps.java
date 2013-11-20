package de.unipotsdam.nexplorer.server.data;

import java.util.HashMap;
import java.util.Map;

import de.unipotsdam.nexplorer.server.PojoAction;

public class Maps {

	public static Map<Object, PojoAction> create(Object key, PojoAction value) {
		Map<Object, PojoAction> result = new HashMap<Object, PojoAction>();
		result.put(key, value);
		return result;
	}

	public static Map<Object, PojoAction> empty() {
		return new HashMap<Object, PojoAction>();
	}
}
