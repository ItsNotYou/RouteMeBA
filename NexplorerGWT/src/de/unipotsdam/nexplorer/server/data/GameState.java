package de.unipotsdam.nexplorer.server.data;

import com.google.inject.Inject;

import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;

public class GameState {

	private DatabaseImpl dbAccess;

	@Inject
	public GameState(DatabaseImpl dbAccess) {
		this.dbAccess = dbAccess;
	}

	public void createFrom(Settings state) {
		dbAccess.persist(state);
	}
}
