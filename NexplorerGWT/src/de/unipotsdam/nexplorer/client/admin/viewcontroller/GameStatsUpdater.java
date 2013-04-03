package de.unipotsdam.nexplorer.client.admin.viewcontroller;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unipotsdam.nexplorer.client.admin.GameStatsBinder;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.GameStatus;

/**
 * This class is called repeatedly in order to update the game stats
 * 
 * @author Julian
 * 
 * @param <T>
 */
public class GameStatsUpdater<T> implements AsyncCallback<GameStats> {

	private GameStatsBinder gameStatsBinder;

	public GameStatsUpdater(GameStatsBinder gameStatsBinder) {
		this.gameStatsBinder = gameStatsBinder;
	}

	@Override
	public void onFailure(Throwable caught) {
		GWT.log("could not update game stats");
	}

	@Override
	public void onSuccess(GameStats result) {
		JSNI.updateGameState(result);
		if (!result.getGameStatus().equals(GameStatus.NOTSTARTED))
			gameStatsBinder.update(result);
	}

}
