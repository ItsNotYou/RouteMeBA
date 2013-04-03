package de.unipotsdam.nexplorer.shared;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UpdatableSettings {

	private Long baseNodeRange;
	private Long itemCollectionRange;
	private Long maxBatteries;
	private Long maxBoosters;

	public Long getBaseNodeRange() {
		return baseNodeRange;
	}

	public void setBaseNodeRange(Long baseNodeRange) {
		this.baseNodeRange = baseNodeRange;
	}

	public Long getItemCollectionRange() {
		return itemCollectionRange;
	}

	public void setItemCollectionRange(Long itemCollectionRange) {
		this.itemCollectionRange = itemCollectionRange;
	}

	public Long getMaxBatteries() {
		return maxBatteries;
	}

	public void setMaxBatteries(Long maxBatteries) {
		this.maxBatteries = maxBatteries;
	}

	public Long getMaxBoosters() {
		return maxBoosters;
	}

	public void setMaxBoosters(Long maxBoosters) {
		this.maxBoosters = maxBoosters;
	}
}
