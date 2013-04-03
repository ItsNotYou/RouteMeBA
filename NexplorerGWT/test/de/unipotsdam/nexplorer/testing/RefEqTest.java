package de.unipotsdam.nexplorer.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Collection;

import org.junit.Test;

public class RefEqTest {

	@Test
	public void shouldFind3Fields() {
		RefWalker<String> sut = new RefWalker<String>("string");
		Collection<Field> result = sut.getAvailableFields(Sub.class);
		assertEquals(3, result.size());
	}

	@Test
	public void shouldFindEqual() {
		Object three = new Object();
		Object one = new Sub("One", 2, three);
		Object two = new Sub("One", 2, three);

		RefWalker<Object> sut = new RefWalker<Object>(one);
		boolean result = sut.matches(two);

		assertTrue(result);
	}

	@Test
	public void shouldUnequalField() {
		Object three = new Object();
		Object one = new Sub("One", 2, three);
		Object two = new Sub("Other", 2, three);

		RefWalker<Object> sut = new RefWalker<Object>(one);
		boolean result = sut.matches(two);

		assertFalse(result);
	}

	@Test
	public void shouldUnequalValue() {
		Object three = new Object();
		Object one = new Sub("One", 2, three);
		Object two = new Sub("One", 10, three);

		RefWalker<Object> sut = new RefWalker<Object>(one);
		boolean result = sut.matches(two);

		assertFalse(result);
	}

	@Test
	public void shouldUnequalTop() {
		Object three = new Object();
		Object one = new Sub("One", 2, three);
		Object two = new Sub("One", 2, null);

		RefWalker<Object> sut = new RefWalker<Object>(one);
		boolean result = sut.matches(two);

		assertFalse(result);
	}

	@Test
	public void shouldUnequalNull() {
		Object three = new Object();
		Object one = new Sub("One", 2, null);
		Object two = new Sub("One", 2, three);

		RefWalker<Object> sut = new RefWalker<Object>(one);
		boolean result = sut.matches(two);

		assertFalse(result);
	}

	@Test
	public void shouldIgnoreAndEqual() {
		Object three = new Object();
		Object one = new Sub("One", 2, null);
		Object two = new Sub("One", 2, three);

		RefWalker<Object> sut = new RefWalker<Object>(one, "top");
		boolean result = sut.matches(two);

		assertTrue(result);
	}

	@Test
	public void shouldFailOnNullMatching() {
		Object one = new Sub("One", 2, null);

		RefWalker<Object> sut = new RefWalker<Object>(one);
		boolean result = sut.matches(null);

		assertFalse(result);
	}
}
