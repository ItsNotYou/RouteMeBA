package de.unipotsdam.nexplorer.server.data;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class PlayerDoesNotExistException extends WebApplicationException {
	public PlayerDoesNotExistException(String message) {
		super(Response.status(Response.Status.BAD_REQUEST).entity(message).type(MediaType.TEXT_PLAIN).build());
	}
	public PlayerDoesNotExistException() {
		super(Response.status(Response.Status.BAD_REQUEST).entity("Player does not exist at this moment").type(MediaType.TEXT_PLAIN).build());
	}
}
