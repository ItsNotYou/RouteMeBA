package de.unipotsdam.nexplorer.client.android.support;


public interface LoginCallback {

	public byte CANCELLED = 0x01;
	public byte SERVER_ERROR = 0x02;

	public void loginSucceeded(Connection connection);

	public void loginFailed(byte reason);
}
