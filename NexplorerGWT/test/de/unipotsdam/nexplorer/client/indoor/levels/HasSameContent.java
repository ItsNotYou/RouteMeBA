package de.unipotsdam.nexplorer.client.indoor.levels;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class HasSameContent extends BaseMatcher<List<Route>> {

	private final List<Route> routes;
	private String problem;

	public HasSameContent(Route... routes) {
		this.routes = Arrays.asList(routes);
	}

	@Override
	public void describeTo(Description arg0) {
		arg0.appendText(problem);
	}

	@Override
	public boolean matches(Object arg0) {
		List<Route> sut = (List<Route>) arg0;
		for (Route element : sut) {
			if (!routes.contains(element)) {
				problem = "Element " + element + " should not be contained";
				return false;
			}
		}

		for (Route element : routes) {
			if (!sut.contains(element)) {
				problem = "Element " + element + " should be contained";
				return false;
			}
		}

		return true;
	}
}
