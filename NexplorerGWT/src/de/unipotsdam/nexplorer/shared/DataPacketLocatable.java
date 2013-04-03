package de.unipotsdam.nexplorer.shared;

import org.codehaus.jackson.annotate.JsonProperty;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;


public class DataPacketLocatable extends DataPacket {

	@JsonProperty("longitude")
	public Double longitude;
	@JsonProperty("latitude")
	public Double latitude;
	@JsonProperty("ownerId")
	public Long ownerId;

	/**
	 * This class allows for json serialization of data packets in order to show them in indoor view we assume that playersByCurrentNodeId contain the geographical data of the packet at a given moment in time
	 * 
	 * @param owner
	 * @param id
	 * @param playersByCurrentNodeId
	 * @param status
	 */
	public DataPacketLocatable(Messager owner, Long id, Players playersByCurrentNodeId, Byte status, MessageDescription messageDescription) {
		super(id, playersByCurrentNodeId, status,messageDescription);
		this.longitude = playersByCurrentNodeId.getLongitude();
		this.latitude = playersByCurrentNodeId.getLatitude();
		this.ownerId = owner.id;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
}
