package de.unipotsdam.nexplorer.client.android.rest;


public class LoginAnswer {

	public static final byte SERVER_ERROR = 1;
	public static final byte NO_GAME_CREATED = 2;

	public Long id;
	public Byte error;
}
