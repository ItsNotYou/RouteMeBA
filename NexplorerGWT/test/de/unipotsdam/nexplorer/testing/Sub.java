package de.unipotsdam.nexplorer.testing;

public class Sub extends Middle {

	private String field;

	public Sub(String field, int value, Object top) {
		this.field = field;
		this.value = value;
		this.top = top;
	}

	public String getField() {
		return this.field;
	}
}
