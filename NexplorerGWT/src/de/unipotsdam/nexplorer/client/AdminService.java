package de.unipotsdam.nexplorer.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.PlayerNotFoundException;
import de.unipotsdam.nexplorer.shared.PlayerStats;

/**
 * die GWT Schnittstelle f�r die Admin Seite
 * @author Julian
 *
 */
@RemoteServiceRelativePath("admin")
public interface AdminService extends RemoteService {

	boolean startGame(GameStats settings);

	boolean stopGame();

	boolean pauseGame();

	GameStats getGameStats();
	
	Settings getDefaultGameStats();

	List<Items> getItemStats();

	PlayerStats getPlayerStats() throws PlayerNotFoundException;
}
