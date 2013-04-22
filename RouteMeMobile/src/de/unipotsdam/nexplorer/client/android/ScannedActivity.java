package de.unipotsdam.nexplorer.client.android;

import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import de.unipotsdam.nexplorer.client.android.sensors.WifiConnector;
import de.unipotsdam.nexplorer.client.android.sensors.WifiListener;

public class ScannedActivity extends Activity implements WifiListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanned);
	}

	@Override
	protected void onStart() {
		super.onStart();

		Uri starter = getIntent().getData();
		Set<String> params = starter.getQueryParameterNames();

		if (params.contains("ssid") && params.contains("password")) {
			String ssid = starter.getQueryParameter("ssid");
			String password = starter.getQueryParameter("password");
			connectToWifi(ssid, password);
		} else {
			redirectToMap();
		}
	}

	private void redirectToMap() {
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}

	private void connectToWifi(String ssid, String password) {
		TextView greetings = (TextView) findViewById(R.id.scanned_grettings);
		TextView ssidView = (TextView) findViewById(R.id.scanned_ssid);

		greetings.setText("Verbinde mit");
		ssidView.setText(ssid);

		new WifiConnector(this).asTask(this).execute(ssid, password);
	}

	@Override
	public void connectSuccessful() {
		redirectToMap();
	}

	@Override
	public void connectFailed() {
		TextView greetings = (TextView) findViewById(R.id.scanned_grettings);
		greetings.setText("Verbindung fehlgeschlagen, bitte erneut versuchen");
	}
}
