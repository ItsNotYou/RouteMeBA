package de.unipotsdam.nexplorer.client.indoor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import de.unipotsdam.nexplorer.client.util.DivElementWrapper;

public class IndoorWrapper extends DivElementWrapper {

	private static IndoorWrapperUiBinder uiBinder = GWT
			.create(IndoorWrapperUiBinder.class);

	interface IndoorWrapperUiBinder extends UiBinder<Widget, IndoorWrapper> {
	}
	@UiField
	HTMLPanel htmlPanel;

	public IndoorWrapper() {
		initWidget(uiBinder.createAndBindUi(this));				
		SimpleIndoorBinder indoorBinder = new SimpleIndoorBinder(this);
		this.htmlPanel.getElement().appendChild(indoorBinder.getElement());
	}
	

}
