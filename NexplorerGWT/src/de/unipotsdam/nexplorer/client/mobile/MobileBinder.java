package de.unipotsdam.nexplorer.client.mobile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;

import de.unipotsdam.nexplorer.client.util.DivElementWrapper;
import de.unipotsdam.nexplorer.client.util.HasWrapper;

public class MobileBinder extends Composite implements HasWrapper {

	private static MobileBinderUiBinder uiBinder = GWT
			.create(MobileBinderUiBinder.class);

	interface MobileBinderUiBinder extends UiBinder<Element, MobileBinder> {
	}
	
	@UiField
	DivElement innerDiv;

	public MobileBinder() {
		setElement(uiBinder.createAndBindUi(this));
		NameEntryBinder nameEntryBinder = new NameEntryBinder();
		setUIObject(nameEntryBinder);
	}
	
	@Override
	public void setUIObject(DivElementWrapper... mobileUIObject) {
		innerDiv.appendChild(mobileUIObject[0].getElement());		
	}
	
	
}
