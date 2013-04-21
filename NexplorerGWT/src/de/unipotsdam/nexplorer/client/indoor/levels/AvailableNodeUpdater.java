package de.unipotsdam.nexplorer.client.indoor.levels;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.JsArrayInteger;

public class AvailableNodeUpdater {

	private static LinkedList<NodeListener> listeners;

	public static void updateAvailableNodes(PlayerMarkers playerMarkers) {
		JsArrayInteger keys = playerMarkers.getKeys();

		List<Node> availableNodes = new LinkedList<Node>();
		for (int count = 0; count < keys.length(); count++) {
			availableNodes.add(new Node(keys.get(count)));
		}

		notifyListeners(availableNodes);
	}

	private static synchronized void ensureListeners() {
		if (listeners == null) {
			listeners = new LinkedList<NodeListener>();
		}
	}

	private static void notifyListeners(List<Node> availableNodes) {
		ensureListeners();
		for (NodeListener listener : listeners) {
			listener.updateAvailableNodes(availableNodes);
		}
	}

	public static void addListener(NodeListener listener) {
		ensureListeners();
		listeners.add(listener);
	}

	public static native void exportStaticMethod() /*-{
		$wnd.updateAvailableNodes = $entry(@de.unipotsdam.nexplorer.client.indoor.levels.AvailableNodeUpdater::updateAvailableNodes(Lde/unipotsdam/nexplorer/client/indoor/levels/PlayerMarkers;));
	}-*/;
}
