package de.unipotsdam.nexplorer.server.data;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class GameHasNotStartedYetException extends WebApplicationException {
	public GameHasNotStartedYetException(String message) {
		super(Response.status(Response.Status.BAD_REQUEST).entity(message)
				.type(MediaType.TEXT_PLAIN).build());
	}

	public GameHasNotStartedYetException() {
		super(Response.status(Response.Status.BAD_REQUEST)
				.entity("Game has not started yet").type(MediaType.TEXT_PLAIN)
				.build());
	}

}
