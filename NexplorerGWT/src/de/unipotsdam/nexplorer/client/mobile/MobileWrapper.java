package de.unipotsdam.nexplorer.client.mobile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import de.unipotsdam.nexplorer.client.util.DivElementWrapper;

/**
 * simples wrappen des html in GWT Klassen
 * eigentlich hätte man es auch direkt in html lassen können, 
 * aber so hat man die Möglichkeit mit GWT Erweiterungen 
 * einzupflegen
 * @author Julian
 *
 */
public class MobileWrapper extends Composite{

	private static MobileWrapperUiBinder uiBinder = GWT
			.create(MobileWrapperUiBinder.class);

	interface MobileWrapperUiBinder extends UiBinder<Widget, MobileWrapper> {
	}
	
	@UiField
	HTMLPanel htmlPanel;

	public MobileWrapper() {
		initWidget(uiBinder.createAndBindUi(this));
		MobileBinder mobileBinder = new MobileBinder();
		this.htmlPanel.getElement().appendChild(mobileBinder.getElement());
	}
	
}
