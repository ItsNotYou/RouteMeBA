package de.unipotsdam.nexplorer.tools.manipulation;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

public class UpdaterFactory {

	private WebResource client;

	public UpdaterFactory(WebResource client) {
		this.client = client;
	}

	public Builder createUpdater() {
		return client.path("update").type(MediaType.APPLICATION_JSON);
	}
}
