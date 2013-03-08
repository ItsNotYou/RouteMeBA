package de.unipotsdam.nexplorer.client.android.tasks;

import android.os.AsyncTask;
import de.unipotsdam.nexplorer.client.android.callbacks.LoginCallback;
import de.unipotsdam.nexplorer.client.android.net.Connection;

public class UserLogin extends AsyncTask<String, Void, Connection> {

	private static String host = "routeme.dnsdynamic.com:8080";
	private LoginCallback callback;

	public UserLogin(LoginCallback callback) {
		this.callback = callback;
	}

	@Override
	protected Connection doInBackground(String... params) {
		if (params.length == 0) {
			return null;
		}

		String name = params[0];
		Connection connection = new Connection(host);
		connection.login(name);

		return connection;
	}

	@Override
	protected void onCancelled(Connection result) {
		callback.loginFailed(LoginCallback.CANCELLED);
	}

	@Override
	protected void onPostExecute(Connection result) {
		callback.loginSucceeded(result);
	}
}
