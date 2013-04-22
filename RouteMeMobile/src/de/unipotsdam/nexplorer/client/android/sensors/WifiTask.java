package de.unipotsdam.nexplorer.client.android.sensors;

import android.os.AsyncTask;

public class WifiTask extends AsyncTask<String, Void, Boolean> {

	private WifiConnector wifi;
	private WifiListener listener;

	public WifiTask(WifiConnector wifi, WifiListener listener) {
		this.wifi = wifi;
		this.listener = listener;
	}

	/**
	 * Takes exactly two strings: Network SSID and PASSWORD.
	 */
	@Override
	protected Boolean doInBackground(String... params) {
		if (params.length != 2) {
			return false;
		}

		return wifi.connectTo(params[0], params[1]);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (result) {
			listener.connectSuccessful();
		} else {
			listener.connectFailed();
		}
	}

	@Override
	protected void onCancelled(Boolean result) {
		listener.connectFailed();
	}
}
