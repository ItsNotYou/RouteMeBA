package de.unipotsdam.nexplorer.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MapAssert {

	public static void assertContains(Object expectedKey, Object expectedValue, Map<? extends Object, ? extends Object> actual) {
		boolean hasMatched = false;
		for (Entry<? extends Object, ? extends Object> tmp : actual.entrySet()) {
			if (expectedKey.getClass() == tmp.getKey().getClass() && matches(expectedKey, tmp.getKey())) {
				assertEquals("Expected value does not match", expectedValue, tmp.getValue());
				hasMatched = true;
			}
		}

		assertTrue(hasMatched);
	}

	private static Collection<Field> getAvailableFields(@SuppressWarnings("rawtypes") Class current) {
		Collection<Field> result = new LinkedList<Field>();

		do {
			List<Field> fields = Arrays.asList(current.getDeclaredFields());
			result.addAll(fields);
			current = current.getSuperclass();
		} while (current != Object.class);

		return result;
	}

	private static boolean shouldIgnore(Field field) {
		return false;
	}

	private static boolean matches(Object sample, Object other) {
		if (other == null) {
			if (sample == null) {
				return true;
			} else {
				return false;
			}
		}

		Collection<Field> fields = getAvailableFields(sample.getClass());
		for (Field field : fields) {
			if (shouldIgnore(field)) {
				continue;
			}

			try {
				field.setAccessible(true);
				Object sampleValue = field.get(sample);
				Object otherValue = field.get(other);

				if (sampleValue == null) {
					if (otherValue != null) {
						return false;
					}
				} else if (!sampleValue.equals(otherValue)) {
					return false;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return false;
			} catch (NullPointerException e) {
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}
}
