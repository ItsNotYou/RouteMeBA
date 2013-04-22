package de.unipotsdam.nexplorer.client.android.sensors;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

/**
 * Mainly taken from <a href="http://stackoverflow.com/questions/8818290/how-to-connect-to-a-specific-wifi-network-in-android-programmatically">stackoverflow</a>
 * 
 * @author hgessner
 * 
 */
public class WifiConnector {

	private WifiManager wifi;

	public WifiConnector(Activity host) {
		this.wifi = (WifiManager) host.getSystemService(Context.WIFI_SERVICE);
	}

	public boolean connectTo(final String ssid, final String password) {
		WifiConfiguration config = new WifiConfiguration();

		String escapedSsid = "\"" + ssid + "\"";
		String escapedPassword = "\"" + password + "\"";

		config.SSID = escapedSsid;
		config.preSharedKey = escapedPassword;
		wifi.addNetwork(config);

		for (WifiConfiguration i : wifi.getConfiguredNetworks()) {
			if (i.SSID != null && i.SSID.equals(escapedSsid)) {
				wifi.disconnect();
				wifi.enableNetwork(i.networkId, true);
				return wifi.reconnect();
			}
		}

		return false;
	}

	public WifiTask asTask(WifiListener listener) {
		return new WifiTask(this, listener);
	}
}
