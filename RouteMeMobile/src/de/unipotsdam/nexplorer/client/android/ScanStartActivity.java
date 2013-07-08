package de.unipotsdam.nexplorer.client.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

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

	private void setIsConnected(boolean isConnected) {
		if (isConnected) {
			forwardToMap();
		}
	}

	private void forwardToMap() {
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}

	public void onScanClicked(View view) {
	}

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
