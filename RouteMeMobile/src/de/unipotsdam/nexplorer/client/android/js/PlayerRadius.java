package de.unipotsdam.nexplorer.client.android.js;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;

public class PlayerRadius {

	public Map map;
	private Circle inner;
	private LatLng latlng;
	private double range;

	public void setCenter(LatLng latLng) {
		this.latlng = latLng;
	}

	public void setMap(Map map) {
		this.map = map;
		if (map == null && inner != null) {
			inner.remove();
		} else if (map != null) {
			map.getMap().addCircle(new CircleOptions().center(latlng.create()).radius(range));
		}
	}

	public void setRadius(double playerRange) {
		this.range = playerRange;
	}
}
