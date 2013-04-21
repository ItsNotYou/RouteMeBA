package de.unipotsdam.nexplorer.client.indoor.view.messaging;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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

	private Route route;

	public RouteBinder(Route route) {
		setElement(uiBinder.createAndBindUi(this));
		source.setInnerText(route.getSource());
		destination.setInnerText(route.getDestination());
		this.route = route;
	}

	@UiHandler("clicker")
	public void onButtonClicked(ClickEvent event) {
		com.google.gwt.user.client.Window.alert("Button pressed");
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
