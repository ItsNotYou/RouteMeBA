package de.unipotsdam.nexplorer.client.indoor.view.messaging;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.UIObject;

public class LevelTwoRouteSelection extends UIObject {

	private static LevelTwoRouteSelectionUiBinder uiBinder = GWT.create(LevelTwoRouteSelectionUiBinder.class);

	interface LevelTwoRouteSelectionUiBinder extends UiBinder<Element, LevelTwoRouteSelection> {
	}

	@UiField
	DivElement routes;

	public LevelTwoRouteSelection() {
		setElement(uiBinder.createAndBindUi(this));
	}
}
