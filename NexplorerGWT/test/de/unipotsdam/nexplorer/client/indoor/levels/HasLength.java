package de.unipotsdam.nexplorer.client.indoor.levels;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class HasLength extends BaseMatcher<List<Route>> {

	private final int size;
	private int actualSize;

	public HasLength(int size) {
		this.size = size;
	}

	@Override
	public boolean matches(Object arg0) {
		List list = (List) arg0;
		actualSize = list.size();
		return list.size() == size;
	}

	@Override
	public void describeTo(Description arg0) {
		arg0.appendText("Length should be " + size + ", but was " + actualSize);
	}
}
