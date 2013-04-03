package de.unipotsdam.nexplorer.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unipotsdam.nexplorer.client.admin.AdminBinder;
import de.unipotsdam.nexplorer.client.admin.viewcontroller.InitialStatsUpdater;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.PlayerStats;

public class AdminServiceImpl implements AdminServiceAsync {

	private final AdminServiceAsync adminServiceAsync = GWT.create(AdminService.class);
	private AdminBinder adminBinder; 
	
	
	public AdminServiceImpl(AdminBinder adminBinder) {
		this.adminBinder = adminBinder;
	}
	
	@Override
	public void stopGame(AsyncCallback<Boolean> callback) {
		adminServiceAsync.stopGame(callback);
		
	}

	@Override
	public void pauseGame(AsyncCallback<Boolean> callback) {
		adminServiceAsync.pauseGame(callback);		
	}

	@Override
	public void getGameStats(AsyncCallback<GameStats> callback) {
		if (callback == null) {
		adminServiceAsync.getGameStats(new InitialStatsUpdater<GameStats>(adminBinder));
		} else {
		adminServiceAsync.getGameStats(callback);
		}
		
	}

	@Override
	public void getItemStats(AsyncCallback<List<Items>> callback) {
		adminServiceAsync.getItemStats(callback);
		
	}

	@Override
	public void getPlayerStats(AsyncCallback<PlayerStats> callback) {
		adminServiceAsync.getPlayerStats(callback);
		
	}

	@Override
	public void startGame(GameStats settings, AsyncCallback<Boolean> callback) {
		this.adminServiceAsync.startGame(settings, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO remove debugging 
				GWT.log("could not start game -dammit");
				GWT.log("for debugging starting anyways now");
				getGameStats(new InitialStatsUpdater<GameStats>(adminBinder));
			}

			@Override
			public void onSuccess(Boolean result) {
				GWT.log("starting Game");	
				getGameStats(new InitialStatsUpdater<GameStats>(adminBinder));
			}
		});
		
	}

	@Override
	public void getDefaultGameStats(AsyncCallback<Settings> callback) {
		adminServiceAsync.getDefaultGameStats(callback);
		
	}



}
