package de.unipotsdam.nexplorer.server.aodv;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.shared.DataPacket;

public class InitialDataPacketMatcher extends BaseMatcher<DataPacket> {

	private boolean hopCountValid;
	private boolean bonusGoalValid;

	@Override
	public boolean matches(Object packet) {
		AodvDataPackets value = (AodvDataPackets) packet;

		Short hops = value.getHopsDone();
		hopCountValid = hops != null && hops == 0;

		Byte bonus = value.getDidReachBonusGoal();
		bonusGoalValid = bonus != null && bonus == 0;

		return hopCountValid && bonusGoalValid;
	}

	@Override
	public void describeTo(Description arg0) {
		if (!hopCountValid)
			arg0.appendText("HopCount should be 0");
		if (!bonusGoalValid)
			arg0.appendText("DidReachBonusGoal should be 0");
	}
}
