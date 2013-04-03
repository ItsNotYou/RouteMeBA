package de.unipotsdam.nexplorer.shared;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class MessageDescription implements Serializable {

	@JsonProperty("sourceNodeId")
	private Long sourceNodeId;
	@JsonProperty("destinationNodeId")
	private Long destinationNodeId;
	@JsonProperty("ownerId")
	private Long ownerId;

	public MessageDescription() {
	}

	public MessageDescription(long source, long dest, long owner) {
		this.sourceNodeId = source;
		this.destinationNodeId = dest;
		this.ownerId = owner;
	}

	public long getSourceNodeId() {
		return sourceNodeId;
	}

	public void setSourceNodeId(Long sourceNodeId) {
		this.sourceNodeId = sourceNodeId;
	}

	public Long getDestinationNodeId() {
		return destinationNodeId;
	}

	public void setDestinationNodeId(Long destinationNodeId) {
		this.destinationNodeId = destinationNodeId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		MessageDescription other = (MessageDescription) obj;
		return eq(this.sourceNodeId, other.sourceNodeId) && eq(this.destinationNodeId, other.destinationNodeId) && eq(this.ownerId, other.ownerId);
	}

	private boolean eq(Long original, Long other) {
		if (original == null) {
			if (other == null) {
				return true;
			} else {
				return false;
			}
		}

		return original.equals(other);
	}
}
