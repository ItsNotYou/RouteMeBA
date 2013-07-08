package de.unipotsdam.nexplorer.client.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanStartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_start);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new OnlineCheckTask().execute();
	}

	/**
	 * Reacts to the given connection status.
	 * 
	 * @param isConnected
	 *            Calls {@link #forwardToMap()} if {@code true}
	 */
	private void setIsConnected(boolean isConnected) {
		if (isConnected) {
			forwardToMap();
		}
	}

	/**
	 * Starts a {@link MapActivity}.
	 */
	private void forwardToMap() {
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}

	/**
	 * Start a scan. As a result, {@link #onActivityResult(int, int, Intent)} is called.
	 * 
	 * @param view
	 */
	public void onScanClicked(View view) {
		IntentIntegrator intent = new IntentIntegrator(this);
		intent.initiateScan();
	}

	/**
	 * Starts a {@link ScannedActivity} if a scan result was given.
	 * 
	 * @see IntentIntegrator#initiateScan()
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null) {
			String result = scanResult.getContents();

			Intent intent = new Intent(this, ScannedActivity.class);
			intent.setData(Uri.parse(result));
			startActivity(intent);
		}
	}

	/**
	 * Calls {@link #forwardToMap()}.
	 * 
	 * @param view
	 */
	public void onForwardClicked(View view) {
		forwardToMap();
	}

	/**
	 * Checks for connectivity.
	 * 
	 * @author Hendrik Geßner
	 */
	private class OnlineCheckTask extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(ScanStartActivity.this, "Netzwerkstatus", "Überprüfe Netzwerkstatus", true);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				NetworkInfo network = manager.getActiveNetworkInfo();
				return network != null && network.isConnected();
			} catch (Exception e) {
				cancel(false);
				return null;
			}
		}

		@Override
		protected void onCancelled(Boolean result) {
			setIsConnected(false);
			dialog.dismiss();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			setIsConnected(result != null && result);
			dialog.dismiss();
		}
	}
}
