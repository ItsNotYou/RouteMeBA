package de.unipotsdam.nexplorer.client.indoor.view.messaging;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.UIObject;

import de.unipotsdam.nexplorer.client.indoor.levels.Route;

public class RouteBinder extends UIObject {

	private static RouteBinderUiBinder uiBinder = GWT.create(RouteBinderUiBinder.class);

	interface RouteBinderUiBinder extends UiBinder<Element, RouteBinder> {
	}

	@UiField
	ButtonElement clicker;
	@UiField
	LabelElement source;
	@UiField
	LabelElement destination;
	@UiField
	ImageElement bonus;

	private final Route route;

	public RouteBinder(Route route) {
		this.route = route;

		setElement(uiBinder.createAndBindUi(this));
		source.setInnerText(route.getSource());
		destination.setInnerText(route.getDestination());
		DOM.sinkEvents(this.getElement(), Event.ONCLICK | Event.ONMOUSEOVER);
	}

	public void setClickHandler(final RouteClickListener handler) {
		DOM.setEventListener(this.getElement(), new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (event.getTypeInt() == Event.ONCLICK) {
					handler.onRouteClick(route);
				} else if (event.getTypeInt() == Event.ONMOUSEOVER) {
					handler.onRouteHovered(route);
				}
			}
		});
	}
}
