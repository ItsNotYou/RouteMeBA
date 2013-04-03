package de.unipotsdam.nexplorer.server.persistence;

import java.util.List;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;
import de.unipotsdam.nexplorer.server.time.Milliseconds;
import de.unipotsdam.nexplorer.shared.GameStatus;

public class Setting {

	private final Settings inner;
	private final DatabaseImpl dbAccess;

	public Setting(Settings inner, DatabaseImpl dbAccess) {
		this.inner = inner;
		this.dbAccess = dbAccess;
	}

	public void save() {
		dbAccess.persist(inner);
	}

	public void decreasePlayingTimeBy(Milliseconds delta) {
		Long remainingPlayingTime = inner().getRemainingPlayingTime();
		remainingPlayingTime -= delta.value();
		remainingPlayingTime = Math.max(0, remainingPlayingTime);
		inner().setRemainingPlayingTime(remainingPlayingTime);
	}

	public boolean noTimeLeft() {
		return inner().getPlayingTime() <= 0;
	}

	/**
	 * Convenience method for inner().getCurrentRoutingMessageProcessingRound()
	 * 
	 * @return
	 */
	public long getCurrentRoutingRound() {
		return inner.getCurrentRoutingMessageProcessingRound();
	}

	/**
	 * Convenience methode for inner().getCurrentDataPacketProcessingRound()
	 * 
	 * @return
	 */
	public Long getCurrentDataRound() {
		return inner.getCurrentDataPacketProcessingRound();
	}

	/**
	 * Convenience methode for inner().setCurrentDataPacketProcessingRound(currentDataPacketProcessingRound)
	 */
	public void setCurrentDataRound(long currentDataPacketProcessingRound) {
		inner.setCurrentDataPacketProcessingRound(currentDataPacketProcessingRound);
	}

	public void setCurrentRoutingRound(long currentRoutingMessageProcessingRound) {
		inner.setCurrentRoutingMessageProcessingRound(currentRoutingMessageProcessingRound);
	}

	public Settings inner() {
		return inner;
	}

	public void findNewBonusGoal() {
		findNewBonusGoal(false);
	}

	public void findNewBonusGoal(boolean forceSearch) {
		Player bonusGoal = null;
		if (inner.getBonusGoal() != null) {
			bonusGoal = dbAccess.getPlayerById(inner.getBonusGoal());
		}

		if (bonusGoal == null || !bonusGoal.hasBattery() || forceSearch) {
			List<Player> randomActiveNodes = dbAccess.getAllActiveNodesInRandomOrder();
			if (randomActiveNodes.size() >= 1) {
				inner.setBonusGoal(randomActiveNodes.get(0).getId());
			} else {
				inner.setBonusGoal(null);
			}
		}
	}

	public void setGameState(GameStatus state) {
		inner().setGameState(state);
	}

	public void incCurrentRoutingRound() {
		long round = getCurrentRoutingRound();
		round++;
		setCurrentRoutingRound(round);
	}

	public void incCurrentDataRound() {
		long round = getCurrentDataRound();
		round++;
		setCurrentDataRound(round);
	}
	
	public Long getBaseNodeRange() {
		return inner.getBaseNodeRange();
	}
}
