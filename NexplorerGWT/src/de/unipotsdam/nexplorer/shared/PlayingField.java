package de.unipotsdam.nexplorer.shared;

import java.io.Serializable;
import java.math.BigDecimal;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;

public class PlayingField implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -778527790808421907L;
	private Location upperLeft;
	private Location lowerRight;

	public PlayingField() {
		// TODO Auto-generated constructor stub
	}

	public PlayingField(Settings settings) {
		this.setLowerRight(new Location(settings.getPlayingFieldLowerRightLatitude(), settings.getPlayingFieldLowerRightLongitude()));
		this.setUpperLeft(new Location(settings.getPlayingFieldUpperLeftLatitude(), settings.getPlayingFieldUpperLeftLongitude()));
	}

	public Location getUpperLeft() {
		return upperLeft;
	}

	public void setUpperLeft(Location upperLeft) {
		this.upperLeft = upperLeft;
	}

	public Location getLowerRight() {
		return lowerRight;
	}

	public void setLowerRight(Location lowerRight) {
		this.lowerRight = lowerRight;
	}

	public Double getCenterOfLatitude() {
		if (!this.getUpperLeft().hasLatitude() || !this.getLowerRight().hasLatitude()) {
			return null;
		}

		BigDecimal upperLeft = new BigDecimal(this.getUpperLeft().getLatitude());
		BigDecimal lowerRight = new BigDecimal(getLowerRight().getLatitude());
		return upperLeft.subtract((((upperLeft.subtract(lowerRight)).divide(new BigDecimal(2))).abs())).doubleValue();
	}

	public Double getCenterOfLongitude() {
		if (!this.getLowerRight().hasLongitude() || !this.getUpperLeft().hasLongitude()) {
			return null;
		}

		return this.getLowerRight().getLongitude() - Math.abs(((this.getUpperLeft().getLongitude() - this.getLowerRight().getLongitude()) / 2));
	}
}
