package de.unipotsdam.nexplorer.client.indoor;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;

import de.unipotsdam.nexplorer.client.util.HasTable;

public class LegendBinder extends HasTable {

	private static LegendBinderUiBinder uiBinder = GWT
			.create(LegendBinderUiBinder.class);

	interface LegendBinderUiBinder extends UiBinder<Element, LegendBinder> {
	}

	@UiField
	TableElement legendTable;
	
	HashMap<String, String> legendItems;		
	
	/**
	 * Dieser Binder zeigt eine Tabelle mit Legendeneinträgen
	 * zu den icons des indoor Spielers 
	 * */
	public LegendBinder() {
		setElement(uiBinder.createAndBindUi(this));
		initLegendItems();		
		insertLegendRows();				 
	}

	/*
	 * 
	 * füllt die Tabelle mit Legendeeinträge
	 */
	private void insertLegendRows() {
		for (String item : legendItems.keySet()) {
			TableRowElement tableRow = this.legendTable.insertRow(-1);			
			Image image = new Image("media/images/icons/"+item);		
			addCell(tableRow, image, -1, "text-align" , "center" );
			addCell(tableRow, legendItems.get(item), -1);
		}
	}

	/**
	 * initialisiere die Map mit den Icons
	 */
	private void initLegendItems() {
		this.legendItems = new HashMap<String, String>();
		this.legendItems.put("network-wireless-small.png", "Spieler / Knoten");
		this.legendItems.put("network-status.png", "Knoten (geringe Auslastung)");
		this.legendItems.put("network-status-away.png", "Knoten (mittlere Auslastung)");
		this.legendItems.put("network-status-busy.png", "Knoten (starke Auslastung)");
		this.legendItems.put("mail.png", "Nachricht unterwegs");
		this.legendItems.put("mail--tick.png", "Nachricht unterwegs");
		this.legendItems.put("mail--exclamation.png", "Wartet auf Wegfindung");
		this.legendItems.put("mail--slash.png",  "Fehler bei der Routensuche");
		this.legendItems.put("mail--clock.png", "Wartet weil Knoten busy");
		this.legendItems.put("flag-white.png", "Startknoten");
		this.legendItems.put("flag-black.png", "Zielknoten");
		this.legendItems.put("star.png", "Bonusziel");
	}

}
