package de.unipotsdam.nexplorer.server.data;

import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.PlayerInternal;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class SetRange implements PlayerInternal {

	private Player node;

	public SetRange(Player node) {
		this.node = node;
	}

	@Override
	public void execute(Players inner) {
		inner.setRange(node.getRange());
	}
}
