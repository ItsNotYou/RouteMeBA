package de.unipotsdam.nexplorer.server.rest;


/**
 * implements game events, since many events in RouteMe are now timer based 
 * the specific methods are now deprecated
 * @author Julian
 */
@Path("game_events")
public class GameEvents {

	private Admin admin;

	public GameEvents() {
		this.admin = new Admin();
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
