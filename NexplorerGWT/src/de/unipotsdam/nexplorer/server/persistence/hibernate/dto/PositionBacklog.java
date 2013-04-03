package de.unipotsdam.nexplorer.server.persistence.hibernate.dto;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.codehaus.jackson.annotate.JsonProperty;

@Entity
@Table(name = "position_backlog")
public class PositionBacklog implements java.io.Serializable {

	private Long id;
	private long playerId;
	private double latitude;
	private double longitude;
	private double accuracy;
	private Double speed;
	private Double heading;
	private Long created;
	private Integer version;

	public PositionBacklog() {
	}

	@JsonProperty("id")
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	@JsonProperty("id")
	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty("playerId")
	@Column(name = "player_id")
	public long getPlayerId() {
		return this.playerId;
	}

	@JsonProperty("playerId")
	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	@JsonProperty("latitude")
	@Column(name = "latitude", precision = 18, scale = 9)
	public double getLatitude() {
		return this.latitude;
	}

	@JsonProperty("latitude")
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@JsonProperty("longitude")
	@Column(name = "longitude", precision = 18, scale = 9)
	public double getLongitude() {
		return this.longitude;
	}

	@JsonProperty("longitude")
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@JsonProperty("accuracy")
	@Column(name = "accuracy", precision = 18, scale = 9)
	public double getAccuracy() {
		return this.accuracy;
	}

	@JsonProperty("accuracy")
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	@JsonProperty("speed")
	@Column(name = "speed", precision = 18, scale = 9)
	public Double getSpeed() {
		return this.speed;
	}

	@JsonProperty("speed")
	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	@JsonProperty("heading")
	@Column(name = "heading", precision = 18, scale = 9)
	public Double getHeading() {
		return this.heading;
	}

	@JsonProperty("heading")
	public void setHeading(Double heading) {
		this.heading = heading;
	}

	@Column(name = "created")
	public Long getCreated() {
		return this.created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	@Version
	@Column(name = "OPTLOCK")
	public Integer getVersion() {
		return this.version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
}
