package de.unipotsdam.nexplorer.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PlayerNotFoundException extends Exception implements IsSerializable {	

	/**
	 * 
	 */
	private static final long serialVersionUID = 8288234538193758171L;

	public PlayerNotFoundException() {
		
	}
	
	public PlayerNotFoundException(long id) {
		super("Unknown player id " + id);
	}

	public PlayerNotFoundException(Throwable e) {
		super(e);
	}
}
