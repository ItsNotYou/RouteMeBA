package de.unipotsdam.nexplorer.client.android.js;

public class Button {

	private final android.widget.Button button;

	public Button(android.widget.Button button) {
		this.button = button;
	}

	public void label(String string) {
		button.setText(string);
	}

	public boolean isDisabled() {
		return !button.isEnabled();
	}

	public void enable() {
		button.setEnabled(true);
	}

	public void disable() {
		button.setEnabled(false);
	}

	public void html(String string) {
		// TODO Change HTML
		button.setText(string);
	}
}
