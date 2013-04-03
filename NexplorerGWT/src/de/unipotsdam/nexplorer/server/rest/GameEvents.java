package de.unipotsdam.nexplorer.server.rest;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import de.unipotsdam.nexplorer.server.Admin;
import de.unipotsdam.nexplorer.server.data.GameHasNotStartedYetException;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.server.rest.dto.OK;
import de.unipotsdam.nexplorer.server.time.BatteryStarter;
import de.unipotsdam.nexplorer.server.time.BonusGoalStarter;
import de.unipotsdam.nexplorer.server.time.ItemPlacingStarter;
import de.unipotsdam.nexplorer.server.time.PlayingTimeStarter;
import de.unipotsdam.nexplorer.shared.ItemMap;
import de.unipotsdam.nexplorer.shared.NodeMap;
import de.unipotsdam.nexplorer.shared.PlayerNotFoundException;

/**
 * implements game events, since many events in RouteMe are now Timer based 
 * the specific methods are now deprecated
 * @author Julian
 *
 */
@Path("game_events")
public class GameEvents {

	private Admin admin;

	public GameEvents() {
		this.admin = new Admin();
	}

	/**
	 * @deprecated See {@link BonusGoalStarter}
	 * @return
	 */
	@POST
	@Path("update_bonus_goals")
	@Produces("application/json")
	public OK updateBonusGoals() {
		System.err.println("Invalide Methode aufgerufen");
		return new OK();
	}

	/**
	 * @deprecated See {@link BatteryStarter}
	 * @return
	 */
	@POST
	@Path("update_node_batteries")
	@Produces("application/json")
	public OK updateNodeBatteries() {
		System.err.println("Invalide Methode aufgerufen");
		return new OK();
	}

	/**
	 * @deprecated See {@link PlayingTimeStarter}
	 * @return
	 */
	@POST
	@Path("update_remaining_playing_time")
	@Produces("application/json")
	public OK updateRemainingPlayingTime() {
		System.err.println("Invalide Methode aufgerufen");
		return new OK();
	}

	/**
	 * @deprecated See {@link ItemPlacingStarter}
	 * @return
	 */
	@POST
	@Path("place_items")
	@Produces("application/json")
	public OK placeItems() {
		System.err.println("Invalide Methode aufgerufen");
		return new OK();
	}

	/**
	 * 
	 * @return
	 */
	@POST
	@Path("get_items")
	@Produces("application/json")
	public ItemMap getItems() {
		List<Items> items = admin.getItemStats();
		return new ItemMap(items);
	}

	@POST
	@Path("get_players")
	@Produces("application/json")
	public NodeMap getPlayers()  {
		try {
		List<Players> nodes = admin.getPlayerStats().getNodes();
		return new NodeMap(nodes);
		} catch (PlayerNotFoundException e) {
			throw new GameHasNotStartedYetException("admin versuchte NodeMap zu laden");
		}
	}
}
