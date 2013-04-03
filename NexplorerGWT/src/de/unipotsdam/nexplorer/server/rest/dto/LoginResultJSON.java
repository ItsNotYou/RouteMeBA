package de.unipotsdam.nexplorer.server.rest.dto;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import de.unipotsdam.nexplorer.server.rest.JSONable;


public class LoginResultJSON extends JSONable<LoginResultJSON> {

	@JsonIgnore
	public static final byte SERVER_ERROR = 1;
	@JsonIgnore
	public static final byte NO_GAME_CREATED = 2;

	@JsonProperty("id")
	public Long id = null;
	@JsonProperty("error")
	public Byte error = null;
	@JsonProperty("errorMessage")
	public String errorMessage = "Es wurde noch kein Spiel erstellt";

	public LoginResultJSON() {
	}

	public LoginResultJSON(long id) {
		this.id = id;
	}

	public LoginResultJSON(byte error) {
		this.error = error;
	}
}
