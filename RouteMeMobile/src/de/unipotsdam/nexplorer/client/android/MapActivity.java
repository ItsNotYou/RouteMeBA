package de.unipotsdam.nexplorer.client.android;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import de.unipotsdam.nexplorer.client.android.js.FunctionsMobile;
import de.unipotsdam.nexplorer.client.android.js.Window;
import de.unipotsdam.nexplorer.client.android.support.MapRotator;
import de.unipotsdam.nexplorer.client.android.ui.UI;

public class MapActivity extends FragmentActivity {

	private FunctionsMobile js;
	private boolean firstStart;
	private LoginDialog loginDialog;
	private static final String HOST_ADRESS = "http://routeme.dnsdynamic.com:8080";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		MapRotator map = new MapRotator(this, R.id.map);
		map.setUpMapIfNeeded(true);
		GoogleMap googleMap = map.getMap();

		loginDialog = new LoginDialog(this);
		loginDialog.setOnLoginListener(new LoginDialog.LoginCallback() {

			@Override
			public void onLogin(String name) {
				js.loginPlayer(name, true);
			}
		});
		firstStart = true;

		Button collectItem = (Button) findViewById(R.id.collectItem);
		Button login = (Button) loginDialog.findViewById(R.id.login_button);
		TextView activeItemsText = (TextView) findViewById(R.id.activeItems);
		TextView hintText = (TextView) findViewById(R.id.hint);
		TextView nextItemDistanceText = (TextView) findViewById(R.id.nextItemDistance);

		TextView beginText = (TextView) loginDialog.findViewById(R.id.login_text);
		TextView score = (TextView) findViewById(R.id.points);
		TextView neighbourCount = (TextView) findViewById(R.id.neighbours);
		TextView remainingPlayingTime = (TextView) findViewById(R.id.time);
		TextView battery = (TextView) findViewById(R.id.battery);

		Dialog waitingForGameDialog = new WaitingDialog(this);
		TextView waitingTextText = (TextView) waitingForGameDialog.findViewById(R.id.waiting_text);

		Dialog noPositionDialog = new WaitingDialog(this);
		((TextView) noPositionDialog.findViewById(R.id.waiting_text)).setText(R.string.default_noposition);

		Window.createInstance(collectItem, login, activeItemsText, hintText, nextItemDistanceText, waitingTextText, this, beginText, score, neighbourCount, remainingPlayingTime, battery, loginDialog, HOST_ADRESS, waitingForGameDialog, noPositionDialog, googleMap, map);
		js = new FunctionsMobile(new UI(this));
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_map, menu);
		return true;
	}
}
