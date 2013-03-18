package de.unipotsdam.nexplorer.client.android.js;

import com.google.android.gms.maps.model.MarkerOptions;

public class Marker {

	public Map map;
	private com.google.android.gms.maps.model.Marker inner = null;
	protected LatLng position;
	protected String title;
	protected MarkerImage icon;
	protected int zIndex;

	public Marker() {
		setData();
	}

	protected void setData() {
	}

	public void setPosition(LatLng latlng) {
		this.position = latlng;
		if (inner != null) {
			inner.setPosition(latlng.create());
		}
	}

	public void setMap(Map map2) {
		this.map = map2;
		if (map2 == null && inner != null) {
			inner.remove();
		} else if (map2 != null) {
			inner = map.getMap().addMarker(new MarkerOptions().position(position.create()).title(title).icon(icon.create()));
		}
	}

	public void setTitle(String string) {
		this.title = string;
		if (inner != null) {
			inner.setTitle(title);
		}
	}
}
