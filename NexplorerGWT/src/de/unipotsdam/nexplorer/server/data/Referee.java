package de.unipotsdam.nexplorer.server.data;

import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

import de.unipotsdam.nexplorer.server.aodv.AodvDataPacket;
import de.unipotsdam.nexplorer.server.di.InjectLogger;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class Referee {

	@InjectLogger
	private Logger logger;
	private DatabaseImpl dbAccess;

	@Inject
	public Referee(DatabaseImpl dbAccess) {
		this.dbAccess = dbAccess;
	}

	public void packetArrived(Setting gameSettings, AodvDataPacket packet) {
		if (packet.inner().getPlayersByCurrentNodeId().getId() == gameSettings.inner().getBonusGoal()) {
			packet.inner().setDidReachBonusGoal((byte) 1);
			gameSettings.findNewBonusGoal(true);
			gameSettings.save();
		}

		int points = 0;
		for (int i = 1; i <= packet.inner().getHopsDone(); i++) {
			points += (i * 10);
		}

		if (packet.inner().getDidReachBonusGoal() != 0) {
			points += Math.round(points * 0.5);
		}
		
		packet.inner().setAwardedScore(points);
		packet.save();
		
		logger.trace("Packet from {} to {} ({} hops, {} bonus goal) is worth {} points", packet.getSource().getId(), packet.getDestination().getId(), packet.inner().getHopsDone(), packet.inner().getDidReachBonusGoal(), points);
		Players owner = packet.inner().getPlayersByOwnerId();
		owner.setScore(owner.getScore() + points);
		logger.trace("Score of player {} inreased to {}", owner.getId(), owner.getScore());
		dbAccess.persist(owner);
	}
}
