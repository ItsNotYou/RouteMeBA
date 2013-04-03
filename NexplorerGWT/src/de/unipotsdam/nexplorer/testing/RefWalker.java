package de.unipotsdam.nexplorer.testing;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.Matchers;
import org.mockito.internal.progress.HandyReturnValues;

public class RefWalker<T> extends BaseMatcher<T> {

	private T sample;
	private String[] except;
	private String matchError;

	public RefWalker(T sample) {
		this(sample, new String[0]);
	}

	public RefWalker(T sample, String... except) {
		this.sample = sample;
		this.except = except;
	}

	public Collection<Field> getAvailableFields(@SuppressWarnings("rawtypes") Class current) {
		Collection<Field> result = new LinkedList<Field>();

		do {
			List<Field> fields = Arrays.asList(current.getDeclaredFields());
			result.addAll(fields);
			current = current.getSuperclass();
		} while (current != Object.class);

		return result;
	}

	@Override
	public boolean matches(Object other) {
		if (other == null) {
			if (sample == null) {
				return true;
			} else {
				matchError = "Object should not be null";
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
						matchError = field.getName() + " should be null";
						return false;
					}
				} else if (!sampleValue.equals(otherValue)) {
					matchError = field.getName() + " should be " + sampleValue + ", but is " + otherValue;
					return false;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				matchError = "Exception while accessing " + field.getName();
				return false;
			} catch (NullPointerException e) {
				e.printStackTrace();
				matchError = "Exception while accessing " + field.getName();
				return false;
			}
		}

		return true;
	}

	private boolean shouldIgnore(Field field) {
		for (int count = 0; count < except.length; count++) {
			if (field.getName().equals(except[count])) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void describeTo(Description desc) {
		desc.appendText(matchError);
	}

	public static <T> T refEq(T sample, String... apart) {
		try {
			Method method = Matchers.class.getDeclaredMethod("reportMatcher", Matcher.class);
			method.setAccessible(true);
			Object result = method.invoke(null, new RefWalker<T>(sample, apart));
			return ((HandyReturnValues) result).returnNull();
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
