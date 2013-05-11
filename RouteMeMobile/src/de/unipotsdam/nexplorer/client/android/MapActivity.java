package de.unipotsdam.nexplorer.client.android;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import de.unipotsdam.nexplorer.client.android.callbacks.UIFooter;
import de.unipotsdam.nexplorer.client.android.callbacks.UIHeader;
import de.unipotsdam.nexplorer.client.android.js.AppWrapper;
import de.unipotsdam.nexplorer.client.android.js.FunctionsMobile;
import de.unipotsdam.nexplorer.client.android.js.Intervals;
import de.unipotsdam.nexplorer.client.android.js.RadiusBlinker;
import de.unipotsdam.nexplorer.client.android.net.RestMobile;
import de.unipotsdam.nexplorer.client.android.sensors.GpsReceiver;
import de.unipotsdam.nexplorer.client.android.sensors.MapRotator;
import de.unipotsdam.nexplorer.client.android.sensors.ShakeDetector;
import de.unipotsdam.nexplorer.client.android.sensors.TouchVibrator;
import de.unipotsdam.nexplorer.client.android.ui.Overlay;
import de.unipotsdam.nexplorer.client.android.ui.Text;
import de.unipotsdam.nexplorer.client.android.ui.UI;

public class MapActivity extends FragmentActivity implements ShakeDetector.ShakeListener {

	private static final String HOST_ADRESS = "http://routeme.dnsdynamic.com:8080";
	private static final boolean IS_DEBUG = true;

	private FunctionsMobile js;
	private boolean firstStart;
	private LoginDialog loginDialog;
	private ShakeDetector shaker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		NexplorerMap mapFragment = (NexplorerMap) getSupportFragmentManager().findFragmentById(R.id.map);
		GoogleMap googleMap = mapFragment.getGoogleMap();
		MapRotator map = mapFragment.getMapRotator();

		UIHeader header = (StatusHeaderFragment) getSupportFragmentManager().findFragmentById(R.id.statusHeader);
		ItemFooterFragment footer = addItemFooter();

		shaker = new ShakeDetector(this, 1, 750);
		shaker.addShakeListener(this);

		loginDialog = new LoginDialog(this);
		loginDialog.setOnLoginListener(new LoginDialog.LoginCallback() {

			@Override
			public void onLogin(String name) {
				js.loginPlayer(name);
			}
		});
		firstStart = true;

		Button login = (Button) loginDialog.findViewById(R.id.login_button);
		TextView beginText = (TextView) loginDialog.findViewById(R.id.login_text);

		Dialog waitingForGameDialog = new WaitingDialog(this);
		TextView waitingTextText = (TextView) waitingForGameDialog.findViewById(R.id.waiting_text);

		Dialog noPositionDialog = new WaitingDialog(this);
		((TextView) noPositionDialog.findViewById(R.id.waiting_text)).setText(R.string.default_noposition);

		RadiusBlinker blinker = new RadiusBlinker(googleMap, this);

		UI ui = createInstance(login, waitingTextText, this, beginText, loginDialog, HOST_ADRESS, waitingForGameDialog, noPositionDialog, googleMap, map, header, footer);

		js = new FunctionsMobile(ui, new AppWrapper(this), new Intervals(new GpsReceiver(this, IS_DEBUG)), mapFragment, new RestMobile(HOST_ADRESS), blinker, new TouchVibrator(this));
	}

	/**
	 * Based on <a href="http://stackoverflow.com/questions/7431516/how-to-change-fragments-class-dynamically">stackoverflow</a>
	 * 
	 * @return
	 */
	private ItemFooterFragment addItemFooter() {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();

		ItemFooterFragment footer = new ItemFooterFragment();
		transaction.replace(R.id.itemFooter, footer);
		transaction.commit();
		return footer;
	}

	public void collectItem(View view) {
		js.collectItem();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (firstStart) {
			loginDialog.show();
			firstStart = false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		shaker.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		shaker.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_map, menu);
		return true;
	}

	@Override
	public void shakeDetected(float accel) {
		js.shakeDetected();
	}

	public static UI createInstance(android.widget.Button login, android.widget.TextView waitingTextText, Activity host, android.widget.TextView beginText, android.app.Dialog loginDialog, String hostAdress, android.app.Dialog waitingForGameDialog, android.app.Dialog noPositionDialog, GoogleMap map, MapRotator rotator, UIHeader header, UIFooter footer) {
		de.unipotsdam.nexplorer.client.android.ui.Button loginButton = new de.unipotsdam.nexplorer.client.android.ui.Button(login, host);

		Text waitingText = new Text(waitingTextText, host);
		Text beginDialog = new Text(beginText, host);

		Overlay loginOverlay = new Overlay(loginDialog, host);

		Overlay waitingForGameOverlay = new Overlay(waitingForGameDialog, host);
		Overlay noPositionOverlay = new Overlay(noPositionDialog, host);

		return new UI(host, loginButton, waitingText, beginDialog, footer, loginOverlay, waitingForGameOverlay, noPositionOverlay, header);
	}
}
