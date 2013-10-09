package de.unipotsdam.nexplorer.client.android.js.tasks;

import android.os.AsyncTask;
import de.unipotsdam.nexplorer.client.android.callbacks.LoginError;
import de.unipotsdam.nexplorer.client.android.callbacks.UILogin;
import de.unipotsdam.nexplorer.client.android.js.FunctionsMobile;
import de.unipotsdam.nexplorer.client.android.net.RestMobile;
import de.unipotsdam.nexplorer.client.android.rest.LoginAnswer;

public class LoginTask extends AsyncTask<String, Void, LoginAnswer> {

	private UILogin uiLogin;
	private RestMobile rest;
	private FunctionsMobile callback;

	public LoginTask(UILogin uiLogin, RestMobile rest, FunctionsMobile callback) {
		this.uiLogin = uiLogin;
		this.rest = rest;
		this.callback = callback;
	}

	@Override
	protected void onPreExecute() {
		uiLogin.loginStarted();
	}

	@Override
	protected LoginAnswer doInBackground(String... params) {
		try {
			String name = params[0];

			LoginAnswer result = rest.login(name);
			if (result.error != null) {
				cancel(false);
			}

			return result;
		} catch (Exception e) {
			cancel(false);
			return null;
		}
	}

	@Override
	protected void onCancelled(LoginAnswer result) {
		if (result != null && result.id == null) {
			uiLogin.loginFailed(LoginError.NO_ID);
		} else {
			uiLogin.loginFailed(LoginError.CAUSE_UNKNOWN);
		}
	}

	@Override
	protected void onPostExecute(LoginAnswer result) {
		uiLogin.loginSucceeded(result.id);
		callback.loginSuccess(result);
	}
}
