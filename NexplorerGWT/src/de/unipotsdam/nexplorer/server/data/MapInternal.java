package de.unipotsdam.nexplorer.server.data;

import de.unipotsdam.nexplorer.server.persistence.PlayerInternal;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class MapInternal implements PlayerInternal {

	private Players mapped;

	@Override
	public void execute(Players inner) throws PlayerDoesNotExistException {
		this.mapped = inner;
	}

	public Players get() {
		return mapped;
	}
}
