package de.unipotsdam.nexplorer.server.time;

import java.util.Date;

import com.google.inject.Inject;

import de.unipotsdam.nexplorer.server.data.Unit;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.shared.GameStatus;

public class SettingsUpdateStarter extends StatelessTimer {

	@Inject
	public SettingsUpdateStarter() {
		super(new Milliseconds(10000), true);
	}

	@Override
	public void started() {
		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Setting settings = dbAccess.getSettings();
			settings.inner().setGameState(GameStatus.ISRUNNING);
			settings.inner().setRunningSince(new Date().getTime());
			settings.inner().setLastPause(null);
			settings.save();
		} finally {
			unit.close();
		}
	}

	@Override
	public void paused() {
		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Setting settings = dbAccess.getSettings();
			settings.inner().setGameState(GameStatus.ISPAUSED);
			settings.save();
		} finally {
			unit.close();
		}
	}

	@Override
	public void resumed() {
		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Setting settings = dbAccess.getSettings();

			logger.trace("Spielstatus vor Update: {}", settings.inner().getGameState());

			settings.inner().setGameState(GameStatus.ISRUNNING);
			settings.inner().setLastPause(new Date().getTime());
			// settings.save();
		} catch (Exception e) {
			logger.catching(e);
		} finally {
			unit.close();
		}

		GameStatus currentStatus = GameStatus.ISRUNNING;
		unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Setting settings = dbAccess.getSettings();
			// hier ist current null gewesen
			currentStatus = settings.inner().getGameState();
			logger.trace("Spielstatus nach Update: {}", currentStatus);
		} finally {
			unit.close();
		}

		if (currentStatus != GameStatus.ISRUNNING)
			resumed();
	}

	@Override
	public void stopped() {
		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Setting settings = dbAccess.getSettings();
			settings.inner().setGameState(GameStatus.HASENDED);
			settings.save();
		} finally {
			unit.close();
		}
	}
}
