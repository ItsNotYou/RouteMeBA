package de.unipotsdam.nexplorer.client.android.rest;

import java.util.Map;

public class Node {

	private String batterieLevel;
	private String neighbourCount;
	private String score;
	private String range;
	private Map<Integer, Neighbour> neighbours;
	private String nearbyItemsCount;
	private NearbyItems nearbyItems;
	private String nextItemDistance;
	private int itemInCollectionRange;
	private String hasRangeBooster;

	public String getBatterieLevel() {
		return batterieLevel;
	}

	public void setBatterieLevel(String batterieLevel) {
		this.batterieLevel = batterieLevel;
	}

	public String getNeighbourCount() {
		return neighbourCount;
	}

	public void setNeighbourCount(String neighbourCount) {
		this.neighbourCount = neighbourCount;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public Map<Integer, Neighbour> getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(Map<Integer, Neighbour> neighbours) {
		this.neighbours = neighbours;
	}

	public String getNearbyItemsCount() {
		return nearbyItemsCount;
	}

	public void setNearbyItemsCount(String nearbyItemsCount) {
		this.nearbyItemsCount = nearbyItemsCount;
	}

	public NearbyItems getNearbyItems() {
		return nearbyItems;
	}

	public void setNearbyItems(NearbyItems nearbyItems) {
		this.nearbyItems = nearbyItems;
	}

	public String getNextItemDistance() {
		return nextItemDistance;
	}

	public void setNextItemDistance(String nextItemDistance) {
		this.nextItemDistance = nextItemDistance;
	}

	public int getItemInCollectionRange() {
		return itemInCollectionRange;
	}

	public void setItemInCollectionRange(int itemInCollectionRange) {
		this.itemInCollectionRange = itemInCollectionRange;
	}

	public String getHasRangeBooster() {
		return hasRangeBooster;
	}

	public void setHasRangeBooster(String hasRangeBooster) {
		this.hasRangeBooster = hasRangeBooster;
	}
}
