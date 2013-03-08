package de.unipotsdam.nexplorer.client.android.net;

public class LoginResult {

	private int id;
	private String errorMessage = null;
	private Byte error = null;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Byte getError() {
		return error;
	}

	public void setError(Byte error) {
		this.error = error;
	}
}
