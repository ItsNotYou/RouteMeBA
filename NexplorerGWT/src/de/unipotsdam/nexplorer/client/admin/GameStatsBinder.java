package de.unipotsdam.nexplorer.client.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import de.unipotsdam.nexplorer.client.util.HasTable;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.GameStatus;

public class GameStatsBinder extends HasTable {

	private static GameStatsBinderUiBinder uiBinder = GWT
			.create(GameStatsBinderUiBinder.class);

	interface GameStatsBinderUiBinder extends
			UiBinder<Element, GameStatsBinder> {
	}

	@UiField
	TableElement gameStatsTable;
	
	@UiField
	DivElement resumeGameButton;
	
//	@UiField
//	SpanElement resetGameButton;

	private AdminBinder adminBinder;

	public GameStatsBinder(AdminBinder adminBinder) {
		setElement(uiBinder.createAndBindUi(this));
		this.adminBinder = adminBinder;
//		if (this.adminBinder.getGameStatus().equals(GameStatus.HASENDED)) {
//			this.resetGameButton.setInnerText("Spiel löschen");			
//		} else {
//			this.resetGameButton.setInnerText("Spiel beenden");
//		}
	}
	
	public DivElement getResumeGameButton() {
		return resumeGameButton;
	}

	public void update(GameStats result) {
		for (int i = 2; i < this.gameStatsTable.getRows().getLength(); i++) {
			this.gameStatsTable.deleteRow(i);
		}

		TableRowElement row = this.gameStatsTable.insertRow(-1);
		String[] textElements = new String[] {
				result.getRunningSince().toString(),
				result.isEnded()+"",
				result.getRemainingPlaytime()/(1000*60) + "",
				result.getPlayingField().getUpperLeft().getLatitude()
						+ "\n "
						+ result.getPlayingField().getUpperLeft()
								.getLongitude(),
				result.getPlayingField().getLowerRight().getLatitude()
						+ "\n "
						+ result.getPlayingField().getLowerRight()
								.getLongitude(),
				result.getCurrentRoutingRound() + "",
				result.getCurrentDataRound() + "" };
		for (int i = 0; i < textElements.length; i++) {
			addCell(row, textElements[i], i);
		}
	}

	public AdminBinder getAdminBinder() {
		return this.adminBinder;		
	}

}
