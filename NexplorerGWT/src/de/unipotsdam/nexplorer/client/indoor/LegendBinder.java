package de.unipotsdam.nexplorer.client.indoor;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;

import de.unipotsdam.nexplorer.client.util.HasTable;

public class LegendBinder extends HasTable {

	private static LegendBinderUiBinder uiBinder = GWT.create(LegendBinderUiBinder.class);
	private static String media = "media/images/icons/";

	interface LegendBinderUiBinder extends UiBinder<Element, LegendBinder> {
	}

	@UiField
	TableElement legendTable;

	private List<LegendPair> legendItems;

	/**
	 * Dieser Binder zeigt eine Tabelle mit Legendeneinträgen zu den icons des indoor Spielers
	 */
	public LegendBinder() {
		setElement(uiBinder.createAndBindUi(this));
		initLegendItems();
		insertLegendRows();
	}

	/**
	 * Füllt die Tabelle mit Legendeeinträge
	 */
	private void insertLegendRows() {
		for (LegendPair item : legendItems) {
			TableRowElement tableRow = this.legendTable.insertRow(-1);
			Image image = new Image(item.getLocation());
			addCell(tableRow, image, -1, "text-align", "center");
			addCell(tableRow, item.getDescription(), -1);
		}
	}

	/**
	 * initialisiere die Map mit den Icons
	 */
	private void initLegendItems() {
		this.legendItems = new ArrayList<LegendPair>();
		this.legendItems.add(new LegendPair(media + "network-wireless-small.png", "Spieler / Knoten"));
		this.legendItems.add(new LegendPair("icons?status=clear&id=0", "Knoten (geringe Auslastung)"));
		this.legendItems.add(new LegendPair("icons?status=away&id=0", "Knoten (mittlere Auslastung)"));
		this.legendItems.add(new LegendPair("icons?status=busy&id=0", "Knoten (starke Auslastung)"));
		this.legendItems.add(new LegendPair(media + "mail.png", "Nachricht unterwegs"));
		this.legendItems.add(new LegendPair(media + "mail--tick.png", "Nachricht unterwegs"));
		this.legendItems.add(new LegendPair(media + "mail--exclamation.png", "Wartet auf Wegfindung"));
		this.legendItems.add(new LegendPair(media + "mail--slash.png", "Fehler bei der Routensuche"));
		this.legendItems.add(new LegendPair(media + "mail--clock.png", "Wartet weil Knoten busy"));
		this.legendItems.add(new LegendPair(media + "flag-white.png", "Startknoten"));
		this.legendItems.add(new LegendPair(media + "flag-black.png", "Zielknoten"));
		this.legendItems.add(new LegendPair(media + "star.png", "Bonusziel"));
	}

	private class LegendPair {

		private String location;
		private String description;

		public LegendPair(String location, String description) {
			this.location = location;
			this.description = description;
		}

		public String getLocation() {
			return this.location;
		}

		public String getDescription() {
			return this.description;
		}
	}
}
