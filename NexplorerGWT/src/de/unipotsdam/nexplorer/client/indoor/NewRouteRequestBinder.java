package de.unipotsdam.nexplorer.client.indoor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Erstellt einen Button im DOM der bei Click resendRoutingMessages() im nativen JS aufruft Also das neu senden der RoutingMessages per broadcast bewirkt
 * 
 * @author Julian
 */
public class NewRouteRequestBinder extends UIObject {

	private static NewRouteRequestUiBinder uiBinder = GWT.create(NewRouteRequestUiBinder.class);

	interface NewRouteRequestUiBinder extends UiBinder<Element, NewRouteRequestBinder> {
	}

	public NewRouteRequestBinder() {
		setElement(uiBinder.createAndBindUi(this));
	}

}
