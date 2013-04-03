package de.unipotsdam.nexplorer.server.persistence;

import de.unipotsdam.nexplorer.server.data.PlayerDoesNotExistException;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public interface PlayerInternal {

	public void execute(Players inner) throws PlayerDoesNotExistException;
}
