package de.unipotsdam.nexplorer.client.indoor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.UIObject;

public class ResetPlayerMessageBinder extends UIObject {

	private static resetPlayerMessageBinderUiBinder uiBinder = GWT
			.create(resetPlayerMessageBinderUiBinder.class);

	interface resetPlayerMessageBinderUiBinder extends
			UiBinder<Element, ResetPlayerMessageBinder> {
	}

	public ResetPlayerMessageBinder() {
		setElement(uiBinder.createAndBindUi(this));
	}

}
