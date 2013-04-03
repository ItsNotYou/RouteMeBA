package de.unipotsdam.nexplorer.client.mobile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.UIObject;

import de.unipotsdam.nexplorer.client.util.DivElementWrapper;

public class NameEntryBinder extends DivElementWrapper {

	private static NameEntryBinderUiBinder uiBinder = GWT
			.create(NameEntryBinderUiBinder.class);

	interface NameEntryBinderUiBinder extends
			UiBinder<Element, NameEntryBinder> {
	}

	public NameEntryBinder() {
		setElement(uiBinder.createAndBindUi(this));
	}

}
