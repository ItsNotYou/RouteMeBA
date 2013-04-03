package de.unipotsdam.nexplorer.client.admin.viewcontroller;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unipotsdam.nexplorer.client.admin.AdminBinder;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;

/**
 * This updater is called only once, when the admin
 * decides to create a game with the date given in the form
 * @author Julian
 *
 * @param <T>
 */
public class DefaultValueUpdater<T> implements AsyncCallback<Settings> {

	private AdminBinder adminBinder;

	public DefaultValueUpdater(AdminBinder adminBinder) {
		this.adminBinder = adminBinder;
	}

	@Override
	public void onFailure(Throwable caught) {
		GWT.log("Default werte konnten nicht geladen werden");

	}

	@Override
	public void onSuccess(Settings result) {
		adminBinder.getNumberOfBatteries().setValue(result.getMaxBatteries()+"");
		adminBinder.getNumberOfBoosters().setValue(result.getMaxBoosters()+"");
		adminBinder.getPlayingFieldLowerRightLatitude().setValue(result.getPlayingFieldLowerRightLatitude()+"");		
		adminBinder.getPlayingFieldLowerRightLongitude().setValue(result.getPlayingFieldLowerRightLongitude()+"");
		adminBinder.getPlayingFieldUpperLeftLatitude().setValue(result.getPlayingFieldUpperLeftLatitude()+"");
		adminBinder.getPlayingFieldUpperLeftLongitude().setValue(result.getPlayingFieldUpperLeftLongitude()+"");
		adminBinder.getDifficulty().setValue(result.getDifficulty()+"");
		adminBinder.getProtocol().setValue(result.getProtocol());
		adminBinder.getRangeForCollectingStuff().setValue(result.getItemCollectionRange()+"");
		adminBinder.getBaseNodeRange().setValue(result.getBaseNodeRange()+"");
		adminBinder.getTimeToPlay().setValue(result.getPlayingTime()+"");		
		adminBinder.getUpdateDisplayIntervalTime().setValue(result.getUpdateDisplayIntervalTime() + "");
		adminBinder.getUpdatePositionIntervalTime().setValue(result.getUpdatePositionIntervalTime() + "");
	}

}
