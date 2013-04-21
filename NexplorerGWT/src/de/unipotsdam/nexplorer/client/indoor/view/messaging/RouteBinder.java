package de.unipotsdam.nexplorer.client.indoor.view.messaging;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Button;
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
	DivElement bonus;

	private final Route route;

	public RouteBinder(Route route) {
		this.route = route;

		setElement(uiBinder.createAndBindUi(this));
		source.setInnerText(route.getSource());
		destination.setInnerText(route.getDestination());

		addClickHandler(new RouteClickListener() {

			@Override
			public void onRouteClick(Route route) {
				com.google.gwt.user.client.Window.alert("Button pressed");
			}
		});
	}

	private void addClickHandler(final RouteClickListener handler) {
		Button button = new Button(clicker.getInnerHTML());
		DivElement divElement = (DivElement) clicker.getParentElement();
		clicker.removeFromParent();
		DOM.sinkEvents(button.getElement(), Event.ONCLICK);
		DOM.setEventListener(button.getElement(), new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				handler.onRouteClick(route);
			}
		});
		divElement.appendChild(button.getElement());
	}

	// public void addClickHandler(final RouteClickListener listener) {
	// clicker.addClickHandler(new ClickHandler() {
	//
	// @Override
	// public void onClick(ClickEvent event) {
	// listener.onRouteClick(route);
	// }
	// });
	// }
}
