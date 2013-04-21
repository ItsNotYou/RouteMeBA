package de.unipotsdam.nexplorer.client.android.maps;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;

import de.unipotsdam.nexplorer.client.android.js.LatLng;
import de.unipotsdam.nexplorer.client.android.js.SenchaMap;
import de.unipotsdam.nexplorer.client.android.rest.Neighbour;

public class NeighbourPing extends TimerTask {

	private static final int frequency = 200;

	private final GoogleMap map;
	private final long repetitions;
	private final Activity host;
	private final LevelTwoNeighbourDrawer callback;
	private final int playerId;

	private LatLng latlng;
	private long currentRepetition;
	private Circle inner;
	private long start;

	public NeighbourPing(SenchaMap senchaMap, Neighbour neighbour, Activity host, LevelTwoNeighbourDrawer callback, int playerId) {
		this.map = senchaMap.map.getMap();
		this.latlng = new LatLng(neighbour.getLatitude(), neighbour.getLongitude());
		this.repetitions = neighbour.getPingDuration() / frequency;
		this.host = host;
		this.inner = null;
		this.callback = callback;
		this.playerId = playerId;

		this.start = new Date().getTime();
		Log.e("ping", "Scheduled " + repetitions + " repetitions");
		new Timer().schedule(this, 0, frequency);
	}

	public void kill() {
		this.cancel();
		host.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (inner != null) {
					inner.remove();
				}
			}
		});
		callback.finishedPing(playerId);

		long end = new Date().getTime();
		Log.e("ping", "Took " + (end - start) + "ms");
	}

	public void update(double latitude, double longitude) {
		this.latlng = new LatLng(latitude, longitude);
	}

	@Override
	public void run() {
		host.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (inner == null) {
					inner = map.addCircle(new CircleOptions().center(latlng.create()).fillColor(Color.TRANSPARENT).strokeColor(Color.BLACK).strokeWidth(5).zIndex(10).radius(1));
					currentRepetition = 0;
				}

				inner.setRadius(currentRepetition % 3 + 1);
				inner.setCenter(latlng.create());
				currentRepetition++;

				if (currentRepetition > repetitions) {
					kill();
				}
			}
		});
	}
}
