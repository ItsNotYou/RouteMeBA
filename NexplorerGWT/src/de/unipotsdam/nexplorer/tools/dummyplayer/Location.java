package de.unipotsdam.nexplorer.tools.dummyplayer;

public class Location {

	private double latitude;
	private double longitude;
	private double accuracy;

	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.accuracy = 5;
	}

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

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!obj.getClass().equals(getClass()))
			return false;

		Location other = (Location) obj;
		return other.getLatitude() == getLatitude() && other.getLongitude() == getLongitude();
	}
}
