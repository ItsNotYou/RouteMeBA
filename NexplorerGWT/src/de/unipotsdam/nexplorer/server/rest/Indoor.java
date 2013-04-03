package de.unipotsdam.nexplorer.server.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unipotsdam.nexplorer.server.data.PlayerDoesNotExistException;
import de.unipotsdam.nexplorer.server.rest.dto.MarkersJSON;
import de.unipotsdam.nexplorer.server.rest.dto.OK;
import de.unipotsdam.nexplorer.shared.DataPacket;
import de.unipotsdam.nexplorer.shared.MessageDescription;
import de.unipotsdam.nexplorer.shared.PlayerInfo;
import de.unipotsdam.nexplorer.shared.PlayerNotFoundException;

/**
 * Diese Klasse verwalted Ajax Request für die Indoor Spieler
 * 
 * @author Julian
 * 
 */
@Path("indoor/")
public class Indoor {

	private de.unipotsdam.nexplorer.server.Indoor indoor;
	private Logger logger;

	public Indoor() {
		this.indoor = new de.unipotsdam.nexplorer.server.Indoor();
		logger = LogManager.getLogger(getClass());
	}

	/**
	 * Gibt die Mobilen Spieler zurück samt ihrer aktuellen Position
	 * 
	 * @param playerId
	 * @return
	 * @throws PlayerDoesNotExistException
	 */
	@GET
	@Path("get_markers")
	@Produces("application/json")
	public MarkersJSON getMarkers(@QueryParam("playerID") String playerId) throws PlayerDoesNotExistException {
		return indoor.getMarkers();
	}

	/**
	 * Setzt die Nachricht zurück und versucht sie nochmal zu senden
	 * 
	 * @param playerId
	 * @return
	 */
	@POST
	@Path("reset_player_message")
	@Produces("application/json")
	public OK resetPlayerMessage(@FormParam("playerId") String playerId) {
		LogManager.getLogger(getClass()).info("Reset player message {}", playerId);
		long id = Long.parseLong(playerId);
		indoor.resetPlayerMessage(id);
		return new OK();
	}

	/**
	 * Route Request wird neu gesendet
	 * 
	 * @param playerId
	 * @return
	 */
	@POST
	@Path("resend_route_request")
	@Produces("application/json")
	public OK resendRouteRequest(@FormParam("playerId") String playerId) {
		LogManager.getLogger(getClass()).info("Resend route request {}", playerId);
		long id = Long.parseLong(playerId);
		indoor.resendRouteRequest(id);
		return new OK();
	}

	/**
	 * Eine neue Nachricht wird abgeschickt
	 * 
	 * @param playerId
	 * @param sourceNodeId
	 * @param destinationNodeId
	 * @return
	 * @throws PlayerNotFoundException
	 * @throws NumberFormatException
	 */
	@POST
	@Path("insert_new_message")
	@Produces("application/json")
	public OK insertNewMessage(@FormParam("ownerId") String playerId, @FormParam("sourceNodeId") String sourceNodeId, @FormParam("destinationNodeId") String destinationNodeId) throws NumberFormatException, PlayerNotFoundException {
		logger.info("Insert new message from {} to {} (owner {})", sourceNodeId, destinationNodeId, playerId);
		long owner = Long.parseLong(playerId);
		long source = Long.parseLong(sourceNodeId);
		long dest = Long.parseLong(destinationNodeId);
		indoor.insertNewMessage(new MessageDescription(source, dest, owner));
		// debugging
		PlayerInfo playerinfo = indoor.getPlayerInfo(Integer.parseInt(playerId));
		if (playerinfo.getDataPacketSend() == null) {
			logger.info("indoor does not get update from packet send. Response Packet is null.");
		} else {
			DataPacket packet = playerinfo.getDataPacketSend();
			// logger.info("Indoor will receive the following status update." + " Status: " + packet.getStatus() + " from: " + packet.getMessageDescription().getSourceNodeId() + " to: " + packet.getMessageDescription().getDestinationNodeId() + " owned by: " + packet.getMessageDescription().getOwnerId());
		}
		return new OK();
	}
}
