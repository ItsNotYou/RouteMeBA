package de.unipotsdam.nexplorer.client.admin;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import de.unipotsdam.nexplorer.client.util.HasTable;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;

/**
 * diese Klasse zeigt Metadaten zu den Items
 * (in der php version habe ich diese
 * Sicht nie gesehen)
 * 
 * @author Julian
 * 
 */
public class ItemStatsBinder extends HasTable {

	private static ItemStatsBinderUiBinder uiBinder = GWT
			.create(ItemStatsBinderUiBinder.class);

	interface ItemStatsBinderUiBinder extends
			UiBinder<Element, ItemStatsBinder> {
	}

	@UiField
	TableElement itemStatsTable;

	public ItemStatsBinder() {
		setElement(uiBinder.createAndBindUi(this));
	}

	/**
	 * transformiere die Items in Tabelleneintr�ge
	 * 
	 * @param result
	 */
	public void update(List<Items> result) {
		clearTable();
		for (Items itemStats : result) {
			TableRowElement row = this.itemStatsTable.insertRow(-1);
			for (int i = 0; i < convert(result.get(0)).length; i++) {
				TableCellElement cell = row.insertCell(i);
				cell.setInnerText(convert(itemStats)[i]);
			}
		}

	}

	private void clearTable() {
			int length = this.itemStatsTable.getRows().getLength();
			for (int i = 2; i < length;  i++) {
				this.itemStatsTable.deleteRow(-1);
			}					
	}

	private String[] convert(Items itemStats) {
		return new String[] { itemStats.getId() + "", itemStats.getName()+"",
				"" + itemStats.getLatitude(), itemStats.getLongitude() + "",
				itemStats.getCreatedAt().toString() };
	}

}
