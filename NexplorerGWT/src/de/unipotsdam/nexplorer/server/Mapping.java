package de.unipotsdam.nexplorer.server;

import de.unipotsdam.nexplorer.server.data.NodeMapper;
import de.unipotsdam.nexplorer.server.data.PlayerDoesNotExistException;
import de.unipotsdam.nexplorer.server.persistence.PlayerInternal;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class Mapping implements PlayerInternal {

	private NodeMapper mapper;
	private Players mapping;

	public Mapping(NodeMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public void execute(Players inner) throws PlayerDoesNotExistException {
		this.mapping = mapper.from(inner);
	}

	public Players getMapping() {
		return mapping;
	}
}
