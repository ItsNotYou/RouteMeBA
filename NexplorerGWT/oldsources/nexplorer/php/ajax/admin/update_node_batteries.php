<?php
	include "../../init.php";

	if (!$gameSettings->didEnd && $gameSettings->isRunning) {
		foreach(Doctrine_Query::create()->from("Player")->where("role = ? AND battery > ?", array(PLAYER_ROLE_NODE, 0))->execute() as $theNode) {
			$theNode->battery = MAX(0, $theNode->battery - (3 + $theNode->Neighbours->count() * 0.5));
			$theNode->save();

			if ($theNode->battery <= 0) {
				// aufräumen wenn Knoten ausgefallen (AODV)
				if ($gameSettings->protocol == "aodv") {
					Doctrine_Query::create()->from("AODVDataPacket")->where("currentNodeId = ?", $theNode->id)->execute()->delete();
					Doctrine_Query::create()->from("AODVRoutingMessage")->where("currentNodeId = ?", $theNode->id)->execute()->delete();
					Doctrine_Query::create()->from("AODVRouteRequestBufferEntry")->where("nodeId = ?", $theNode->id)->execute()->delete();
					Doctrine_Query::create()->from("AODVRoutingTableEntry")->where("nodeId = ?", $theNode->id)->execute()->delete();
				}
			}
			
			$theNode->score += $theNode->Neighbours->count() * 10;
			$theNode->save();
		}

		// Spielende wenn nur noch ein Knoten übrig
		if (Doctrine_Query::create()->from("Player")->where("role = ? AND battery > ?", array(PLAYER_ROLE_NODE, 0))->execute()->count() <= 1) {
			$gameSettings->didEnd = 1;
			$gameSettings->save();
		}	
	}

	$conn->commit();
?>