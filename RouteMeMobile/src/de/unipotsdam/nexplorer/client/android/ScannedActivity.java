package de.unipotsdam.nexplorer.client.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ScannedActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanned);
	}

	@Override
	protected void onStart() {
		super.onStart();

		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}
}
