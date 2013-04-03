package de.unipotsdam.nexplorer.client.indoor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.UIObject;

public class SimpleIndoorBinder extends UIObject {

	private static SimpleIndoorBinderUiBinder uiBinder = GWT
			.create(SimpleIndoorBinderUiBinder.class);

	interface SimpleIndoorBinderUiBinder extends
			UiBinder<Element, SimpleIndoorBinder> {
	}
	
	@UiField
	DivElement legendDiv;
	
	@UiField
	DivElement playerInfoContainer;

	private IndoorWrapper indoorWrapper;

	public SimpleIndoorBinder(IndoorWrapper indoorWrapper) {
		setElement(uiBinder.createAndBindUi(this));	
		LegendBinder legendBinder = new LegendBinder();
		legendDiv.appendChild(legendBinder.getElement());
		PlayerInfoBinder playerInfoBinder = new PlayerInfoBinder(this);
		playerInfoContainer.appendChild(playerInfoBinder.getElement());
		this.indoorWrapper = indoorWrapper;
	}
	
	public IndoorWrapper getIndoorWrapper() {
		return indoorWrapper;
	}
	
	
}
