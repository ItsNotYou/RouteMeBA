package de.unipotsdam.nexplorer.client.indoor;

import de.unipotsdam.nexplorer.client.indoor.levels.Route;
import de.unipotsdam.nexplorer.client.indoor.view.messaging.RouteClickListener;

public class LevelTwoHandler implements RouteClickListener {

	@Override
	public void onRouteClick(Route route) {
		int src = Integer.parseInt(route.getSource());
		int dest = Integer.parseInt(route.getDestination());

		setRoute(src, dest);
		insertNewMessage();
	}

	private native void setRoute(int src, int dest)/*-{
		var srcPos = $wnd.playerMarkersArray[src].getPosition();
		var destPos = $wnd.playerMarkersArray[dest].getPosition();

		$wnd.setSourceFlag(src, srcPos);
		$wnd.setDestinationFlag(dest, destPos);
	}-*/;

	private native void insertNewMessage()/*-{
		$wnd.insertNewMessage();
	}-*/;

	@Override
	public void onRouteHovered(Route route) {
		int src = Integer.parseInt(route.getSource());
		int dest = Integer.parseInt(route.getDestination());

		hoverRoute(src, dest);
	}

	private native void hoverRoute(int src, int dest)/*-{
		$wnd.hoveredSourceNode = src;
		$wnd.hoveredDestinationNode = dest;
	}-*/;
}
