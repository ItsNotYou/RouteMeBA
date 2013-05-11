package de.unipotsdam.nexplorer.client.android.callbacks;

public interface UILogin {

	public void loginStarted();

	public void loginSucceeded(long playerId);

	public void loginFailed(LoginError reason);
}
