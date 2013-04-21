package de.unipotsdam.nexplorer.client.indoor.view.messaging;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import de.unipotsdam.nexplorer.client.indoor.levels.Route;
import de.unipotsdam.nexplorer.client.indoor.levels.RouteListener;

public class LevelTwoRouteSelection extends RoutingLevel implements RouteListener, RouteClickListener {

	private static LevelTwoRouteSelectionUiBinder uiBinder = GWT.create(LevelTwoRouteSelectionUiBinder.class);

	interface LevelTwoRouteSelectionUiBinder extends UiBinder<Element, LevelTwoRouteSelection> {
	}

	@UiField
	DivElement routes;

	private final HashMap<Route, RouteBinder> routesModel;
	private final Collection<RouteClickListener> clickObservers;

	public LevelTwoRouteSelection() {
		setElement(uiBinder.createAndBindUi(this));
		this.routesModel = new HashMap<Route, RouteBinder>();
		this.clickObservers = new LinkedList<RouteClickListener>();
	}

	@Override
	public void updateRoutes(List<Route> routes) {
		removeObsolete(routes);
		addNew(routes);
	}

	private void removeObsolete(List<Route> routes) {
		Iterator<Entry<Route, RouteBinder>> viewedRoutes = routesModel.entrySet().iterator();
		while (viewedRoutes.hasNext()) {
			Entry<Route, RouteBinder> viewedRoute = viewedRoutes.next();
			if (!routes.contains(viewedRoute.getKey())) {
				viewedRoute.getValue().getElement().removeFromParent();
				viewedRoutes.remove();
			}
		}
	}

	private void addNew(List<Route> routes) {
		for (Route route : routes) {
			if (!routesModel.containsKey(route)) {
				RouteBinder view = new RouteBinder(route);
				this.routes.appendChild(view.getElement());
				routesModel.put(route, view);
			}
		}
	}

	public void addClickHandler(final RouteClickListener listener) {
		this.clickObservers.add(listener);
	}

	@Override
	public void onRouteClick(Route route) {
		for (RouteClickListener listener : clickObservers) {
			listener.onRouteClick(route);
		}
	}
}
