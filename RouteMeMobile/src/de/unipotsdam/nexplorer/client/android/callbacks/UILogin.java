package de.unipotsdam.nexplorer.client.android.callbacks;

public interface UILogin {

	public void loginStarted(String name);

	public void loginSucceeded(int playerId);

	public void loginFailed(LoginError reason);
}
