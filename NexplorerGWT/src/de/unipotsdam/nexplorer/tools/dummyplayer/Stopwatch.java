package de.unipotsdam.nexplorer.tools.dummyplayer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Stopwatch {

	private static Map<String, List<Long>> data;

	static {
		data = new HashMap<String, List<Long>>();
	}

	private String method;
	private long startMillis;

	private Stopwatch(String method) {
		this.method = method;
	}

	private void start() {
		this.startMillis = System.currentTimeMillis();
	}

	public void stop() {
		long stopMillis = System.currentTimeMillis();
		long delta = stopMillis - startMillis;
		insertData(method, delta);
	}

	public static Stopwatch start(String method) {
		Stopwatch watch = new Stopwatch(method);
		watch.start();
		return watch;
	}

	public synchronized static void printStatus() {
		for (Entry<String, List<Long>> deltas : data.entrySet()) {
			long sum = 0;
			for (Long delta : deltas.getValue()) {
				sum += delta;
			}

			long median = sum / deltas.getValue().size();
			System.out.println(deltas.getKey() + " took " + median + "ms");
		}

		System.out.println();
	}

	private synchronized static void insertData(String method, long delta) {
		if (!data.containsKey(method)) {
			data.put(method, new LinkedList<Long>());
		}

		if (data.get(method).size() >= 5) {
			data.get(method).remove(0);
		}
		data.get(method).add(delta);
	}
}
