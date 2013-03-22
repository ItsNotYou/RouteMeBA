package de.unipotsdam.nexplorer.client.android.js;

public class LatLng {

	private double latitude;
	private double longitude;

	public LatLng(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public LatLng(de.unipotsdam.nexplorer.client.android.support.Location location) {
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
	}

	public com.google.android.gms.maps.model.LatLng create() {
		return new com.google.android.gms.maps.model.LatLng(latitude, longitude);
	}
}
