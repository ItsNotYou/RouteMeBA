package de.unipotsdam.nexplorer.client.admin.viewcontroller;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

import de.unipotsdam.nexplorer.client.admin.AdminBinder;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.GameStatus;

/**
 * This class takes all the data from the form and
 * sends it to the server in order to  be processed
 * This should normally be a click-listener wrapping the
 * submit_button, however
 * if you take normal html code and use black magic 
 * to wrap a button this seems to be the only way
 * that works in chrome at least
 * @author Julian
 */
public class GameStartedHandler implements EventListener {

	private AdminBinder adminBinder;

	public GameStartedHandler(AdminBinder adminBinder) {
		this.adminBinder = adminBinder;
	}

	@Override
	public void onBrowserEvent(Event event) {
		transferGameData();			
	}

	private void transferGameData() {
		Long updateDisplayTime = Long.parseLong(adminBinder
				.getUpdateDisplayIntervalTime().getValue().trim());
		Long updatePositionTime = Long.parseLong(adminBinder
				.getUpdatePositionIntervalTime().getValue().trim());
		Long difficultyLong = Long.parseLong(adminBinder.getDifficulty()
				.getValue().trim());
		Long playingTimeLong = Long.parseLong(adminBinder.getTimeToPlay()
				.getValue().trim());
		Long maxBoosterLong = Long.parseLong(adminBinder.getNumberOfBoosters()
				.getValue().trim());
		Long timeStarted = null;
		Long maxBatteries = Long.parseLong(adminBinder.getNumberOfBatteries()
				.getValue().trim());
		Long baseNodeRangeLong = Long.parseLong(adminBinder.getBaseNodeRange()
				.getValue().trim());
		Long itemCollectionLong = Long.parseLong(adminBinder
				.getRangeForCollectingStuff().getValue().trim());
		Double playingFieldLeftUpperLatitude = Double.parseDouble(adminBinder
				.getPlayingFieldUpperLeftLatitude().getValue().trim());
		Double playingFieldUpperLeftLongitude = Double.parseDouble(adminBinder
				.getPlayingFieldUpperLeftLongitude().getValue().trim());
		Double playingFieldLowerRightLatitude = Double.parseDouble(adminBinder
				.getPlayingFieldLowerRightLatitude().getValue().trim());
		Double playingFieldLowerRightLongitude = Double.parseDouble(adminBinder
				.getPlayingFieldLowerRightLongitude().getValue().trim());
		String protocol = adminBinder.getProtocol().getOptions()
				.getItem(adminBinder.getProtocol().getSelectedIndex())
				.getText();
		this.adminBinder.getAdminService().startGame(
				new GameStats(new Settings((byte) 1, timeStarted, 0l, (byte) 0,
						difficultyLong, playingTimeLong, 0l, protocol,
						baseNodeRangeLong, itemCollectionLong,
						playingFieldLeftUpperLatitude,
						playingFieldUpperLeftLongitude,
						playingFieldLowerRightLatitude,
						playingFieldLowerRightLongitude, maxBatteries,
						maxBoosterLong, 0l, 0l, 0l, updatePositionTime,
						updateDisplayTime)), null);
	}

}
