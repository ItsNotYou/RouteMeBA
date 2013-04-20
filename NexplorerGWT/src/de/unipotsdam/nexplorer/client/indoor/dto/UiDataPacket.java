package de.unipotsdam.nexplorer.client.indoor.dto;

import de.unipotsdam.nexplorer.shared.DataPacket;

public class UiDataPacket {

	private final String sourceNodeId;
	private final String destinationNodeId;
	private final Byte status;
	private final String awardedScore;
	private final String currentNodeId;

	public UiDataPacket(DataPacket dataPacketSend) {
		this.sourceNodeId = dataPacketSend.getMessageDescription().getSourceNodeId() + "";
		this.destinationNodeId = dataPacketSend.getMessageDescription().getDestinationNodeId() + "";
		this.currentNodeId = dataPacketSend.getPlayersByCurrentNodeId().getId() + "";
		this.status = dataPacketSend.getStatus();
		this.awardedScore = dataPacketSend.getAwardedScore() + "";
	}

	public String getSourceNodeId() {
		return this.sourceNodeId;
	}

	public String getDestinationNodeId() {
		return this.destinationNodeId;
	}

	public Byte getStatus() {
		return this.status;
	}

	public String getAwardedScore() {
		return this.awardedScore;
	}

	public String getCurrentNodeId() {
		return this.currentNodeId;
	}
}
