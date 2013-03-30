package de.unipotsdam.nexplorer.client.android.rest;

import java.util.Map;

public class Node {

	private Double batterieLevel;
	private Integer neighbourCount;
	private Integer score;
	private Integer range;
	private Map<Integer, Neighbour> neighbours;
	private Integer nearbyItemsCount;
	private NearbyItems nearbyItems;
	private Integer nextItemDistance;
	private int itemInCollectionRange;
	private Integer hasRangeBooster;

	public Double getBatterieLevel() {
		return batterieLevel;
	}

	public void setBatterieLevel(Double batterieLevel) {
		this.batterieLevel = batterieLevel;
	}

	public Integer getNeighbourCount() {
		return neighbourCount;
	}

	public void setNeighbourCount(Integer neighbourCount) {
		this.neighbourCount = neighbourCount;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getRange() {
		return range;
	}

	public void setRange(Integer range) {
		this.range = range;
	}

	public Map<Integer, Neighbour> getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(Map<Integer, Neighbour> neighbours) {
		this.neighbours = neighbours;
	}

	public Integer getNearbyItemsCount() {
		return nearbyItemsCount;
	}

	public void setNearbyItemsCount(Integer nearbyItemsCount) {
		this.nearbyItemsCount = nearbyItemsCount;
	}

	public NearbyItems getNearbyItems() {
		return nearbyItems;
	}

	public void setNearbyItems(NearbyItems nearbyItems) {
		this.nearbyItems = nearbyItems;
	}

	public Integer getNextItemDistance() {
		return nextItemDistance;
	}

	public void setNextItemDistance(Integer nextItemDistance) {
		this.nextItemDistance = nextItemDistance;
	}

	public boolean isItemInCollectionRangeBoolean() {
		return itemInCollectionRange != 0;
	}

	public int getItemInCollectionRange() {
		return itemInCollectionRange;
	}

	public void setItemInCollectionRange(int itemInCollectionRange) {
		this.itemInCollectionRange = itemInCollectionRange;
	}

	public Boolean hasRangeBoosterBoolean() {
		if (hasRangeBooster == null) {
			return null;
		} else {
			return hasRangeBooster != 0;
		}
	}

	public Integer getHasRangeBooster() {
		return hasRangeBooster;
	}

	public void setHasRangeBooster(Integer hasRangeBooster) {
		this.hasRangeBooster = hasRangeBooster;
	}
}
