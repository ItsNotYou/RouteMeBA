package de.unipotsdam.nexplorer.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

import de.unipotsdam.nexplorer.client.admin.AdminWrapper;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NexplorerGWT implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {		
		AdminWrapper adminBinder = new AdminWrapper();		
		RootPanel.get("contents").add((adminBinder));
	}
}
