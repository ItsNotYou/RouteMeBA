package de.unipotsdam.nexplorer.server.persistence;

import java.util.Collection;

import com.google.gwt.thirdparty.guava.common.base.Predicate;
import com.google.gwt.thirdparty.guava.common.collect.Collections2;

public class IsNotIn implements Predicate<Player> {

	private Collection<Long> knownIds;

	public IsNotIn(Collection<Player> knownElements) {
		IdTransformation toId = new IdTransformation();
		knownIds = Collections2.transform(knownElements, toId);
	}

	@Override
	public boolean apply(Player element) {
		return !knownIds.contains(element.getId());
	}
}
