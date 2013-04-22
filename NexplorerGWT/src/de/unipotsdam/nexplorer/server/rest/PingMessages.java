package de.unipotsdam.nexplorer.server.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.unipotsdam.nexplorer.client.android.rest.PingRequest;
import de.unipotsdam.nexplorer.client.android.rest.PingResponse;

@Path("ping/")
public class PingMessages {

	private de.unipotsdam.nexplorer.server.Mobile mobile;

	public PingMessages() {
		this.mobile = new de.unipotsdam.nexplorer.server.Mobile();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PingResponse addPing(PingRequest request) {
		System.out.println("Ping from " + request.getNodeId() + " received");
		return mobile.addPing(request);
	}
}
