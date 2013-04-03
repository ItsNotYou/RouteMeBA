package de.unipotsdam.nexplorer.shared;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.gwt.user.client.rpc.IsSerializable;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

/**
 * This class is used in order to transfer Datapackets via JSON corresponding classes are AODVDataPackets for the DB and MessageRequest for the XML-RPC
 * 
 * @author Julian
 * 
 */
public class DataPacket implements IsSerializable {

	@JsonProperty("id")
	protected Long id;
	@JsonIgnore
	protected Players playersByCurrentNodeId;
	@JsonProperty("status")
	protected Byte status;
	@JsonProperty("messageDescription")
	public MessageDescription messageDescription;
	private Integer awardedScore;

	public DataPacket() {
		this.messageDescription = new MessageDescription();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MessageDescription getMessageDescription() {
		return messageDescription;
	}

	public void setMessageDescription(MessageDescription messageDescription) {
		this.messageDescription = messageDescription;
	}

	protected DataPacket(Long id, Players playersByCurrentNodeId, Byte status, MessageDescription messageDescription) {
		this.id = id;
		this.playersByCurrentNodeId = playersByCurrentNodeId;
		this.status = status;
		this.messageDescription = messageDescription;
	}

	@JsonIgnore
	public Players getPlayersByCurrentNodeId() {
		return this.playersByCurrentNodeId;
	}

	@JsonIgnore
	public void setPlayersByCurrentNodeId(Players playersByCurrentNodeId) {
		this.playersByCurrentNodeId = playersByCurrentNodeId;
	}

	public Byte getStatus() {
		return this.status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public void setPlayersByDestinationId(Players playersByDestinationId) {
		if (playersByDestinationId != null) {
			this.messageDescription.setDestinationNodeId(playersByDestinationId.getId());
		} else {
			this.messageDescription.setDestinationNodeId(null);
		}
	}

	public void setPlayersBySourceId(Players playersBySourceId) {
		if (playersBySourceId != null) {
			this.messageDescription.setSourceNodeId(playersBySourceId.getId());
		} else {
			this.messageDescription.setSourceNodeId(null);
		}
	}

	public void setPlayersByOwnerId(Players playersByOwnerId) {
		if (playersByOwnerId != null) {
			this.messageDescription.setOwnerId(playersByOwnerId.getId());
		} else {
			this.messageDescription.setOwnerId(null);
		}
	}

	public Integer getAwardedScore() {
		return this.awardedScore;
	}
	
	public void setAwardedScore(Integer score) {
		this.awardedScore = score;
	}
}