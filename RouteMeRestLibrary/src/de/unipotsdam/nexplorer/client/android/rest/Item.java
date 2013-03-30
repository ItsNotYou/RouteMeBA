package de.unipotsdam.nexplorer.client.android.rest;

public class Item {

	private String itemType;
	private double latitude;
	private double longitude;

	public String getItemType() {
		return this.itemType;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
