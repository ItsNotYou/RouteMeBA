package de.unipotsdam.nexplorer.client.admin.viewcontroller;

import com.google.gwt.user.client.Timer;

import de.unipotsdam.nexplorer.client.AdminServiceImpl;
import de.unipotsdam.nexplorer.client.admin.AdminBinder;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.PlayerStats;

/**
 * Ein cron job für die updates
 * 
 * @author Julian
 * 
 */
public class StatsUpdateTimer extends Timer {

	private AdminServiceImpl adminService;
	private AdminBinder adminBinder;

	public StatsUpdateTimer(AdminServiceImpl adminService,
			AdminBinder adminBinder) {
		this.adminService = adminService;
		this.adminBinder = adminBinder;
	}

	@Override
	public void run() {
		this.adminService.getPlayerStats(new PlayerStatsUpdater<PlayerStats>(
				adminBinder));
		this.adminService
				.getItemStats(new ItemStatsUpdater<Items>(adminBinder));
		this.adminService.getGameStats(new GameStatsUpdater<GameStats>(
				adminBinder.getGameStatsBinder()));
	}

}
