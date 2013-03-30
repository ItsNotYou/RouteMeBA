package de.unipotsdam.nexplorer.client.android.rest;

public class Settings {

	private Integer isRunning;
	private Integer itemCollectionRange;
	private Integer updateDisplayIntervalTime;

	public Boolean isRunningBoolean() {
		if (isRunning == null) {
			return null;
		} else {
			return isRunning != 0;
		}
	}

	public Integer getIsRunning() {
		return isRunning;
	}

	public void setIsRunning(Integer isRunning) {
		this.isRunning = isRunning;
	}

	public Integer getItemCollectionRange() {
		return itemCollectionRange;
	}

	public void setItemCollectionRange(Integer itemCollectionRange) {
		this.itemCollectionRange = itemCollectionRange;
	}

	public Integer getUpdateDisplayIntervalTime() {
		return updateDisplayIntervalTime;
	}

	public void setUpdateDisplayIntervalTime(Integer updateDisplayIntervalTime) {
		this.updateDisplayIntervalTime = updateDisplayIntervalTime;
	}
}
