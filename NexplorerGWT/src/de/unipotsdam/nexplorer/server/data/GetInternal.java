package de.unipotsdam.nexplorer.server.data;

import de.unipotsdam.nexplorer.server.persistence.PlayerInternal;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class GetInternal implements PlayerInternal {

	private Players inner;

	@Override
	public void execute(Players inner) {
		this.inner = inner;
	}

	public Players get() {
		return inner;
	}
}
