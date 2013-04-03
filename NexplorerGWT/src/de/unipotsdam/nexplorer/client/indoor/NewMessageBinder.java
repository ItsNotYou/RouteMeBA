package de.unipotsdam.nexplorer.client.indoor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.UIObject;

/**
 * erstellt im DOM einen Button der
 * resetPlayerMessage() im nativen JS aufruft
 * @author Julian
 *
 */
public class NewMessageBinder extends UIObject {

	private static NewMessageBinderUiBinder uiBinder = GWT
			.create(NewMessageBinderUiBinder.class);

	interface NewMessageBinderUiBinder extends
			UiBinder<Element, NewMessageBinder> {
	}

	public NewMessageBinder() {
		setElement(uiBinder.createAndBindUi(this));
	}

}
