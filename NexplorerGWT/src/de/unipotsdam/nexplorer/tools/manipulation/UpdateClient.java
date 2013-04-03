package de.unipotsdam.nexplorer.tools.manipulation;

import de.unipotsdam.nexplorer.shared.UpdatableSettings;

public class UpdateClient {

	private UpdaterFactory updater;
	private UpdatableSettings update;
	private boolean isAutoUpdate;

	public UpdateClient(UpdaterFactory updater, UpdatableSettings initialSettings) {
		this.updater = updater;
		this.update = initialSettings;
	}

	public void sendUpdate() {
		updater.createUpdater().post(update);
	}

	public void setBaseNodeRange(int value) {
		update.setBaseNodeRange((long) value);
		if (isAutoUpdate)
			sendUpdate();
	}

	public void setItemCollectionRange(int value) {
		update.setItemCollectionRange((long) value);
		if (isAutoUpdate)
			sendUpdate();
	}

	public void setMaxBatteries(int value) {
		update.setMaxBatteries((long) value);
		if (isAutoUpdate)
			sendUpdate();
	}

	public void setMaxBoosters(int value) {
		update.setMaxBoosters((long) value);
		if (isAutoUpdate)
			sendUpdate();
	}

	public void setAutoMode(boolean isAuto) {
		this.isAutoUpdate = isAuto;
	}

	public int getBaseNodeRange() {
		return update.getBaseNodeRange().intValue();
	}

	public int getItemCollectionRange() {
		return update.getItemCollectionRange().intValue();
	}

	public int getMaxBatteries() {
		return update.getMaxBatteries().intValue();
	}

	public int getMaxBoosters() {
		return update.getMaxBoosters().intValue();
	}
}
