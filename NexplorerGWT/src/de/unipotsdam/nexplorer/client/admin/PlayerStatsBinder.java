package de.unipotsdam.nexplorer.client.admin;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import de.unipotsdam.nexplorer.client.util.HasTable;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.shared.Messager;
import de.unipotsdam.nexplorer.shared.PlayerStats;

public class PlayerStatsBinder extends HasTable {

	private static PlayerStatsBinderUiBinder uiBinder = GWT.create(PlayerStatsBinderUiBinder.class);

	interface PlayerStatsBinderUiBinder extends UiBinder<Element, PlayerStatsBinder> {
	}

	@UiField
	TableElement mobileStatsTable;

	@UiField
	TableElement indoorStatsTable;

	public PlayerStatsBinder() {
		setElement(uiBinder.createAndBindUi(this));
	}

	/**
	 * fill tables with player stats
	 * 
	 * @param result
	 */
	public void update(PlayerStats result) {
		int length = this.mobileStatsTable.getRows().getLength();
		for (int i = 2; i < length; i++) {
			this.mobileStatsTable.deleteRow(-1);
		}

		int indoorLength = this.indoorStatsTable.getRows().getLength();
		for (int i = 2; i < indoorLength; i++) {
			this.indoorStatsTable.deleteRow(-1);
		}

		for (Messager messagers : result.getMessagers()) {
			TableRowElement row = this.indoorStatsTable.insertRow(-1);
			for (int i = 0; i < convertMessagers(result.getMessagers().get(0)).length; i++) {
				TableCellElement cell = row.insertCell(i);
				cell.setInnerText(convertMessagers(messagers)[i]);
			}
		}

		for (Players node : result.getNodes()) {
			TableRowElement row = this.mobileStatsTable.insertRow(-1);
			for (int i = 0; i < convertNodes(result.getNodes().get(0)).length; i++) {
				TableCellElement cell = row.insertCell(i);
				cell.setInnerText(convertNodes(node)[i]);
			}
		}
	}

	private String[] convertMessagers(Messager messagers) {
		return new String[] { messagers.id + "", messagers.name, messagers.score + "" };
	}

	private String[] convertNodes(Players node) {
		Date lastPositionUpdate = null;
		if (node.getLastPositionUpdate() != null) {
			lastPositionUpdate = new Date();
			lastPositionUpdate.setTime(node.getLastPositionUpdate());
		}
		return new String[] { node.id + "", node.name, node.getLatitude() + "", node.getLongitude() + "", lastPositionUpdate != null ? lastPositionUpdate.toString() : "-", node.score + "", node.getBattery() + "", node.getBoosterSince() != null ? node.getBoosterSince().toString() : "-" };
	}
}
