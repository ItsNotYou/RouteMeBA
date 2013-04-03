package de.unipotsdam.nexplorer.shared;

import java.io.Serializable;

/**
 * custom data holder for the gui
 * 
 * @author Julian
 * 
 */
public class PlayerLocation extends Location implements Serializable {

	private static final long serialVersionUID = -5943599222335010262L;

	private long playerId;
	private double accuracy;
	private Double speed;
	private Double heading;

	public PlayerLocation() {
	}

	public PlayerLocation(long playerId, double latitude, double longitude, double accuracy) {
		this(playerId, latitude, longitude, accuracy, null, null);
	}

	public PlayerLocation(long playerId, double latitude, double longitude, double accuracy, Double speed, Double heading) {
		super(latitude, longitude);
		this.playerId = playerId;
		this.accuracy = accuracy;
		this.speed = speed;
		this.heading = heading;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Double getHeading() {
		return heading;
	}

	public void setHeading(Double heading) {
		this.heading = heading;
	}
}
