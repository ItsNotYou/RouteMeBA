package de.unipotsdam.nexplorer.client.util;

import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.UIObject;

public abstract class HasTable extends UIObject {

	public HasTable() {
		super();
	}
	
	protected void addCell(TableRowElement row, String text, int index) {
		TableCellElement cell1 = row.insertCell(index);
		cell1.setInnerText(text);
	}
	

	protected void addCell(TableRowElement row, String text, int index, String styleName, String styleValue) {
		TableCellElement cell1 = row.insertCell(index);
		cell1.setInnerText(text);
		cell1.setPropertyString(styleName, styleValue);		
	}
	
	protected void addCell(TableRowElement row, Image image, int index, String styleName, String styleValue) {
		TableCellElement cell1 = row.insertCell(index);
		cell1.appendChild(image.getElement());
		cell1.setPropertyString(styleName, styleValue);		
	}


}