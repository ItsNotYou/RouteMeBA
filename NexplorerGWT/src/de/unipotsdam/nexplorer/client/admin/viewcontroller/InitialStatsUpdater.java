package de.unipotsdam.nexplorer.client.admin.viewcontroller;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unipotsdam.nexplorer.client.admin.AdminBinder;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.GameStatus;

/**
 * Dise Klasse kümmert sich darum, 
 * dass der aktuelle Zustand des Spieles aus der Datenbank geholt wird und angezeigt wird. 
 * Vergleiche andere RPC Update Klassen.
 * 
 * @author Julian
 * 
 * @param <T>
 */
public class InitialStatsUpdater<T> implements AsyncCallback<GameStats> {

	private AdminBinder adminBinder;

	public InitialStatsUpdater(AdminBinder adminBinder) {
		this.adminBinder = adminBinder;
	}

	@Override
	public void onFailure(Throwable caught) {
		GWT.log("could not update gamestats");
	}

	@Override
	public void onSuccess(GameStats result) {
		updateAfterStart(result);
		// for faster update one direct call
		JSNI.updateGameState(result);
	}


	
	/**
	 * 
	 * @param result
	 */
	public void updateAfterStart(GameStats result) {
		GWT.log(result.getGameStatus()+"");
		if (!result.getGameStatus().equals(GameStatus.NOTSTARTED)) {
			adminBinder.showGameStats(); // will also show canvas
			adminBinder.showItemStats();
			adminBinder.showPlayerStats();
			adminBinder.startGameForm.removeFromParent();
			//shows map with players
			showCanvas(result);
		}		
	
		//starts updater callback
		adminBinder.startUpdateIntevals(1000l);
	}

	

	/**
	 * canvas hinzufügen	 
	 * @param gameStats
	 */
	public void showCanvas(GameStats gameStats) {
		if (gameStats != null) {
			adminBinder.getMap_canvas().appendChild(adminBinder.getMyMapCanvas().getElement());
			adminBinder.getMyMapCanvas().showField(gameStats.getPlayingField());
			adminBinder.getMyMapCanvas().setVisible(true);
		} else {
			GWT.log("kann Canvas nicht ohne Koordinaten zeigne");
		}
	}

}
