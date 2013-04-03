package de.unipotsdam.nexplorer.server.persistence.hibernate.dto;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.unipotsdam.nexplorer.shared.ItemMap;
import de.unipotsdam.nexplorer.shared.MessagerInterface;

public interface PlayerInterface extends MessagerInterface {

	@JsonIgnore
	@Transient
	public abstract List<Players> getNeighbours();

	@JsonIgnore
	public abstract void setNeighbours(List<Players> neighbours);

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public abstract Long getId();

	public abstract void setId(Long id);

	@Column(name = "role")
	public abstract Byte getRole();

	public abstract void setRole(Byte role);

	@Column(name = "name", length = 45)
	public abstract String getName();

	public abstract void setName(String name);

	@Column(name = "score")
	public abstract Long getScore();

	public abstract void setScore(Long score);

	@Column(name = "latitude", precision = 18, scale = 9)
	public abstract Double getLatitude();

	public abstract void setLatitude(Double latitude);

	@Column(name = "longitude", precision = 18, scale = 9)
	public abstract Double getLongitude();

	public abstract void setLongitude(Double longitude);

	@Column(name = "last_position_update")
	public abstract Long getLastPositionUpdate();

	public abstract void setLastPositionUpdate(Long lastPositionUpdate);

	@Column(name = "battery", precision = 5)
	public abstract Double getBattery();

	public abstract void setBattery(Double battery);

	@Column(name = "has_signal_range_booster")
	public abstract Long getHasSignalRangeBooster();

	public abstract void setHasSignalRangeBooster(Long hasSignalRangeBooster);

	@Column(name = "has_signal_strength_booster")
	public abstract Long getHasSignalStrengthBooster();

	public abstract void setHasSignalStrengthBooster(
			Long hasSignalStrengthBooster);

	@Column(name = "remaining_high_priority_messages")
	public abstract Integer getRemainingHighPriorityMessages();

	public abstract void setRemainingHighPriorityMessages(
			Integer remainingHighPriorityMessages);

	@Column(name = "sequence_number")
	public abstract Long getSequenceNumber();

	public abstract void setSequenceNumber(Long sequenceNumber);

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "playersByCurrentNodeId")
	public abstract Set<AodvDataPackets> getAodvDataPacketsesForCurrentNodeId();

	public abstract void setAodvDataPacketsesForCurrentNodeId(
			Set<AodvDataPackets> aodvDataPacketsesForCurrentNodeId);

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "players")
	public abstract Set<AodvNodeData> getAodvNodeDatas();

	public abstract void setAodvNodeDatas(Set<AodvNodeData> aodvNodeDatas);

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "node")
	public abstract Set<Neighbours> getNeighbourses();

	public abstract void setNeighbourses(Set<Neighbours> neighbourses);

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "neighbour")
	public abstract Set<Neighbours> getNodesForNeighbour();

	public abstract void setNodesForNeighbour(Set<Neighbours> nodesForNeighbour);

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "playersByDestinationId")
	public abstract Set<AodvDataPackets> getAodvDataPacketsesForDestinationId();

	public abstract void setAodvDataPacketsesForDestinationId(
			Set<AodvDataPackets> aodvDataPacketsesForDestinationId);

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "playersByOwnerId")
	public abstract Set<AodvDataPackets> getAodvDataPacketsesForOwnerId();

	public abstract void setAodvDataPacketsesForOwnerId(
			Set<AodvDataPackets> aodvDataPacketsesForOwnerId);

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "playersBySourceId")
	public abstract Set<AodvDataPackets> getAodvDataPacketsesForSourceId();

	public abstract void setAodvDataPacketsesForSourceId(
			Set<AodvDataPackets> aodvDataPacketsesForSourceId);

	/**
	 * setting the items also as jsonable 
	 * @param items
	 */
	public abstract void setNearbyItems(List<Items> items);

	public abstract void setItemInCollectionRange(Long long1);

	@Transient
	public abstract long getRange();

	public abstract void setRange(long range);

	@Transient
	public abstract int getNeighbourCount();

	public abstract void setNeighbourCount(int neighbourCount);

	@Transient
	public abstract int getNearbyItemsCount();

	public abstract void setNearbyItemsCount(int nearbyItemsCount);

	@Transient
	public abstract Long getNextItemDistance();

	public abstract void setNextItemDistance(Long nextItemDistance);

	@Transient
	public abstract int getPacketCount();

	public abstract void setPacketCount(int packetCount);

	@Transient
	public abstract Date getBoosterSince();

	public abstract void setBoosterSince(Date boosterSince);

	@Transient
	public abstract ItemMap getNearbyItemsJSON();

	@Transient
	public abstract Long getItemInCollectionRange();

}