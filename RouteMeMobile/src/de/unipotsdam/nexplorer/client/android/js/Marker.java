package de.unipotsdam.nexplorer.client.android.js;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;

import de.unipotsdam.nexplorer.client.android.ui.UIElement;

public class Marker extends UIElement {

	public GoogleMap map;
	private com.google.android.gms.maps.model.Marker inner = null;
	protected LatLng position;
	protected String title = "";
	protected MarkerImage icon;
	protected int zIndex;

	public Marker(Activity context) {
		super(context);
		setData();
		if (map != null) {
			setMap(map);
		}
	}

	protected void setData() {
	}

	public void setPosition(final LatLng latlng) {
		this.position = latlng;
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				if (inner != null) {
					inner.setPosition(latlng.create());
				}
			}
		});
	}

	public void setMap(final GoogleMap map2) {
		this.map = map2;
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				if (map2 == null && inner != null) {
					inner.remove();
				} else if (map2 != null) {
					inner = map2.addMarker(new MarkerOptions().position(position.create()).title(title).icon(icon.create()).anchor(icon.getU(), icon.getV()));
				}
			}
		});
	}

	public void setTitle(String string) {
		this.title = string;
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				if (inner != null) {
					inner.setTitle(title);
				}
			}
		});
	}
}
