package de.unipotsdam.nexplorer.server.persistence;

import com.google.gwt.thirdparty.guava.common.base.Function;

public class IdTransformation implements Function<Player, Long> {

	@Override
	public Long apply(Player player) {
		return player.getId();
	}
}
