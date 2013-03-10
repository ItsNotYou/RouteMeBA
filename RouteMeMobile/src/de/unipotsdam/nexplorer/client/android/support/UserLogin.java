package de.unipotsdam.nexplorer.client.android.support;

import android.os.AsyncTask;

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
