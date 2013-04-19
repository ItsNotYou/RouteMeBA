package de.unipotsdam.nexplorer.client.indoor.viewcontroller;

import com.google.gwt.user.client.Timer;

import de.unipotsdam.nexplorer.client.IndoorServiceImpl;
import de.unipotsdam.nexplorer.client.indoor.PlayerInfoBinder;

/**
 * cron job für die aktualisierung der Indoor Oberfläche
 * 
 * @author Julian
 * 
 */
public class IndoorStatsTimer extends Timer {

	private final PlayerInfoBinder playerInfoBinder;
	private final IndoorServiceImpl indoorServiceImpl;

	public IndoorStatsTimer(PlayerInfoBinder playerInfoBinder) {
		this.playerInfoBinder = playerInfoBinder;
		this.indoorServiceImpl = new IndoorServiceImpl();
	}

	@Override
	public void run() {
		indoorServiceImpl.getPlayerInfo(getId(), new PlayerInfoUpdater(this.playerInfoBinder));
	}

	private native int getId() /*-{
		return $wnd.getPlayerId();
	}-*/;

	private native void setID(Long id) /*-{
		$wnd.setPlayerId(id);
	}-*/;
}
