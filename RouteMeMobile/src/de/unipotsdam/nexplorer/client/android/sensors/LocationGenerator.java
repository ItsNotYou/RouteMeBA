package de.unipotsdam.nexplorer.client.android.sensors;

import java.util.Random;

import com.grum.geocalc.BoundingArea;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.DegreeCoordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;

/**
 * Based on the library geocalc at {@link http://www.grumlimited.co.uk/}.
 * 
 * @author hgessner
 */
public class LocationGenerator {

	private static final int NUMBER_OF_START_PARTS = 10;

	private double upperLeftLongitude;
	private double upperLeftLatitude;
	private double lowerRightLongitude;
	private double lowerRightLatitude;

	private double lastLatitude;
	private double lastLongitude;
	private double lastDirection;

	private BoundingArea playingField;
	private Random random;

	public LocationGenerator(double upperLeftLatitude, double upperLeftLongtiude, double lowerRightLatitude, double lowerRightLongitude) {
		this.upperLeftLongitude = upperLeftLongtiude;
		this.upperLeftLatitude = upperLeftLatitude;
		this.lowerRightLongitude = lowerRightLongitude;
		this.lowerRightLatitude = lowerRightLatitude;
		this.random = new Random();

		Coordinate lat = new DegreeCoordinate(upperLeftLatitude);
		Coordinate lon = new DegreeCoordinate(lowerRightLongitude);
		Point upperLeft = new Point(lat, lon);

		lat = new DegreeCoordinate(lowerRightLatitude);
		lon = new DegreeCoordinate(upperLeftLongtiude);
		Point lowerRight = new Point(lat, lon);

		this.playingField = new BoundingArea(upperLeft, lowerRight);
	}

	public android.location.Location generateStartLocation(int direction) {
		double deltaLatitude = lowerRightLatitude - upperLeftLatitude;
		double deltaLongitude = lowerRightLongitude - upperLeftLongitude;

		double baseLatitude = deltaLatitude / NUMBER_OF_START_PARTS * (random.nextInt(NUMBER_OF_START_PARTS - 1) + 1);
		double baseLongitude = deltaLongitude / NUMBER_OF_START_PARTS * (random.nextInt(NUMBER_OF_START_PARTS - 1) + 1);

		saveLocation(upperLeftLatitude + baseLatitude, upperLeftLongitude + baseLongitude, direction);
		return getLastLocation();
	}

	public android.location.Location getLastLocation() {
		android.location.Location result = new android.location.Location("gps");
		result.setLatitude(lastLatitude);
		result.setLongitude(lastLongitude);
		result.setAccuracy(5);
		return result;
	}

	private void saveLocation(double latitude, double longitude, double direction) {
		this.lastLatitude = latitude;
		this.lastLongitude = longitude;
		this.lastDirection = direction;
	}

	public void setStartDirection(double latitude, double longitude, double direction) {
		saveLocation(latitude, longitude, direction);
	}

	public android.location.Location generateNextLocation(double distance, double direction) {
		Coordinate lat = new DegreeCoordinate(this.lastLatitude);
		Coordinate lon = new DegreeCoordinate(this.lastLongitude);
		Point last = new Point(lat, lon);

		Point other = generatePointInField(distance, direction);

		double otherLatitude = other.getLatitude();
		double otherLongitude = other.getLongitude();
		double otherDirection = EarthCalc.getBearing(last, other);
		saveLocation(otherLatitude, otherLongitude, otherDirection);

		return getLastLocation();
	}

	private Point generatePointInField(double distance, double direction) {
		Coordinate lat = new DegreeCoordinate(this.lastLatitude);
		Coordinate lon = new DegreeCoordinate(this.lastLongitude);
		Point last = new Point(lat, lon);
		direction += lastDirection + 360;
		direction %= 360;

		Point other = EarthCalc.pointRadialDistance(last, direction, distance);
		if (playingField.isContainedWithin(other)) {
			return other;
		} else {
			return generatePointInField(distance, random.nextInt(360));
		}
	}
}
