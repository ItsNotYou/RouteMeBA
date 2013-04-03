package de.unipotsdam.nexplorer.client.util;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;

public abstract class DivElementWrapper extends Composite {

	public DivElementWrapper() {
		super();
	}

	public void attach() {
		/*
		 * Widget.onAttach() is protected
		 */
		onAttach();
	
		/*
		 * mandatory for all widgets without parent widget
		 */
		RootPanel.detachOnWindowClose(this);
	}
	
	public void attach_p() {
		/*
		 * Widget.onAttach() is protected
		 */
		onAttach();	
	}
	
	/**
	 * This method should only be used, if a div Element is replaced by a widget (proper gwt ui binder widget)
	 * If an html binder is attached, use divElement.addChild....
	 * @param divElement
	 */
	public void replaceParentDiv(DivElement divElement) {
		divElement.getParentElement().replaceChild(this.getElement(), divElement);
		attach();
	}
		

}