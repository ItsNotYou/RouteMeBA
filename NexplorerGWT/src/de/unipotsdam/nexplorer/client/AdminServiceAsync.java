package de.unipotsdam.nexplorer.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.PlayerStats;

public interface AdminServiceAsync {

	void startGame(GameStats settings, AsyncCallback<Boolean> callback);

	void stopGame(AsyncCallback<Boolean> callback);

	void pauseGame(AsyncCallback<Boolean> callback);

	void getGameStats(AsyncCallback<GameStats> callback);

	void getItemStats(AsyncCallback<List<Items>> callback);

	void getPlayerStats(AsyncCallback<PlayerStats> callback);

	void getDefaultGameStats(AsyncCallback<Settings> callback);
}
