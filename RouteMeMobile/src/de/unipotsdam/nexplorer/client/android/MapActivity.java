package de.unipotsdam.nexplorer.client.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import de.unipotsdam.nexplorer.client.android.js.FunctionsMobile;
import de.unipotsdam.nexplorer.client.android.support.MapRotator;

public class MapActivity extends FragmentActivity {

	private FunctionsMobile js;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		new MapRotator(this, R.id.map).setUpMapIfNeeded();
		js = new FunctionsMobile();
	}

	@Override
	protected void onStart() {
		super.onStart();

		LoginDialog login = new LoginDialog(this);
		login.setOnLoginListener(new LoginDialog.LoginCallback() {

			@Override
			public void onLogin(String name) {
				finish();
			}
		});
		login.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_map, menu);
		return true;
	}
}
