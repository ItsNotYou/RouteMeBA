package de.unipotsdam.nexplorer.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;

public class PlayerInfo implements IsSerializable {

	private Messager player;
	private Settings settings;
	private DataPacket dataPacketSend;

	public PlayerInfo() {
	}

	public PlayerInfo(Messager player, Settings settings, DataPacket dataPacketSend) {
		this.player = player;
		this.settings = settings;
		this.dataPacketSend = dataPacketSend;
	}

	public Messager getPlayer() {
		return player;
	}

	public void setPlayer(Messager player) {
		this.player = player;
	}

	public Long getRemainingTime() {
		return settings.getRemainingPlayingTime();
	}

	public DataPacket getDataPacketSend() {
		return dataPacketSend;
	}

	public void setDataPacketSend(DataPacket dataPacketSend) {
		this.dataPacketSend = dataPacketSend;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	/**
	 * Gibt eine Unterstütztende Nachricht je nach Situation des Spielers aus
	 * 
	 * @return
	 */
	public String getHint() {
		// TODO Auto-generated method stub
		return null;
	}

	public Long getUpdateDisplayTime() {
		return settings.getUpdateDisplayIntervalTime();
	}

	public String getBonusGoal() {
		if (settings.getBonusGoal() == null) {
			return "";
		} else {
			return settings.getBonusGoal() + "";
		}
	}
}
