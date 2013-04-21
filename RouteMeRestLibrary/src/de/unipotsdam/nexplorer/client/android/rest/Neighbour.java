package de.unipotsdam.nexplorer.client.android.rest;

public class Neighbour {

	private double latitude;
	private double longitude;
	private boolean pingActive;
	private long pingDuration;

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public boolean isPingActive() {
		return pingActive;
	}

	public void setPingActive(boolean pingActive) {
		this.pingActive = pingActive;
	}

	public long getPingDuration() {
		return pingDuration;
	}

	public void setPingDuration(long pingDuration) {
		this.pingDuration = pingDuration;
	}
}
