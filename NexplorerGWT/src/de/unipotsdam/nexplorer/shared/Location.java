package de.unipotsdam.nexplorer.shared;

import java.io.Serializable;

/**
 * This class is a util class for location
 * 
 * @author Julian Dehne and Hendrik Geßner
 * 
 */
public class Location implements Locatable, Serializable {

	private static final long serialVersionUID = -5263606794755856130L;

	private double latitude = 0.;
	private double longitude = 0.;
	private boolean hasLatitude = false;
	private boolean hasLongitude = false;

	public Location() {
	}

	public Location(Double latitude, Double longitude) {
		this.setLatitude(latitude);
		this.setLongitude(longitude);
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		if (latitude == null) {
			this.hasLatitude = false;
		} else {
			this.latitude = latitude;
			this.hasLatitude = true;
		}
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		if (longitude == null) {
			this.hasLongitude = false;
		} else {
			this.longitude = longitude;
			this.hasLongitude = true;
		}
	}

	public boolean hasLatitude() {
		return hasLatitude;
	}

	public boolean hasLongitude() {
		return hasLongitude;
	}
}
