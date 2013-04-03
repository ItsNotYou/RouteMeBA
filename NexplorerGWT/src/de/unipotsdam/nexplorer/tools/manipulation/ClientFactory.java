package de.unipotsdam.nexplorer.tools.manipulation;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import de.unipotsdam.nexplorer.shared.UpdatableSettings;

public class ClientFactory {

	public UpdateClient login(String host) {
		WebResource client = Client.create().resource("http://" + host).path("rest").path("settings");

		try {
			UpdatableSettings initialSettings = client.path("read").accept(MediaType.APPLICATION_JSON).get(UpdatableSettings.class);
			return new UpdateClient(new UpdaterFactory(client), initialSettings);
		} catch (Exception e) {
			return null;
		}
	}
}
