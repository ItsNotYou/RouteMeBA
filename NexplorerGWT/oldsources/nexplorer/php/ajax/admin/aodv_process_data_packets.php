<?php
	header("Content-Type: text/html; charset=utf-8");
	
	if (file_exists("../../init.php")) {
		include "../../init.php";
	}
	
	// alle Knoten bearbeiten welche noch im Spiel sind (zufällige Reihenfolge)
	$nodes = Doctrine_Query::create()->select("*, RANDOM() AS rand")->from("Player")->where("role = ? AND battery > ?", array(PLAYER_ROLE_NODE, 0))->orderBy("rand")->execute();
	
	if (!$isUnitTest) echo "------------adovProcessDataPackets Runde ".$gameSettings->currentRoutingMessageProcessingRound." ".date("m.d.Y H:i:s")."----------------\n";

	foreach($nodes as $theNode) {
		// alle Datenpackete des Knotens bearbeiten
		if (!$isUnitTest) echo "***Datenpakete bei Knoten {$theNode->id}***\n";
	
		// ältestes Paket zuerst bearbeiten
		$dataPackets = Doctrine_Query::create()->from("AODVDataPacket")->where("currentNodeId = ? AND status != ? AND processingRound = ?", array($theNode->id, AODV_DATA_PACKET_STATUS_ARRIVED, $gameSettings->currentDataPacketProcessingRound))->orderBy("id asc")->execute();
		$packetCount = 1;
		foreach($dataPackets as $thePacket) {
			// Nur das erste Paket bearbeiten und alle anderen in Wartestellung setzen
			if ($packetCount == 1) {
				// Pakete ist unterwegs oder wartet darauf versendet zu werden
				if ($thePacket->status == AODV_DATA_PACKET_STATUS_UNDERWAY || $thePacket->status == AODV_DATA_PACKET_STATUS_NODE_BUSY) {
					// prüfen ob Route zum Ziel bekannt
					$theRoute = $theNode->getRouteToDestination($thePacket->destinationId);
					if (!empty($theRoute)) {
						// Packet weitersenden
						$theNode->forwardDataPacketOnRoute($thePacket, $theRoute, $isUnitTest);
					} else {
						// RERRs senden (jemand denkt irrtümlich ich würde eine Route kennen)
						$theNode->sendRouteErrorToNeighbours($thePacket->destinationId, $isUnitTest);
					}
					// Packet löschen
					// Debugging
					if (!$isUnitTest) echo "Datenpaket mit sourceId {$thePacket->sourceId} und destinationId {$thePacket->destinationId} löschen, weil fertig bearbeitet.\n";

					$thePacket->delete();
				} else if ($thePacket->status == AODV_DATA_PACKET_STATUS_WAITING_FOR_ROUTE || $thePacket->status == AODV_DATA_PACKET_STATUS_ERROR) {
					// Paket ist in Wartestellung (Route war anfänglich unbekannt)
					// prüfen ob mittlerweile Route zum Ziel bekannt
					$theRoute = $theNode->getRouteToDestination($thePacket->destinationId);
					if (!empty($theRoute)) {
						// Packet weitersenden
						$theNode->forwardDataPacketOnRoute($thePacket, $theRoute, $isUnitTest);

						// Debugging
						if (!$isUnitTest) echo "Datenpaket mit sourceId {$thePacket->sourceId} und destinationId {$thePacket->destinationId} löschen, weil fertig bearbeitet.\n";

						$thePacket->delete();
					} else {
						$RREQCount = Doctrine_Query::create()->from("AODVRoutingMessage")->where("type = ? AND sourceId = ? AND destinationId = ?", array(AODV_ROUTING_MESSAGE_TYPE_RREQ, $thePacket->sourceId, $thePacket->destinationId))->execute()->count();
						if ($RREQCount == 0) {
							$thePacket->status = AODV_DATA_PACKET_STATUS_ERROR;
						} else {
							$thePacket->status = AODV_DATA_PACKET_STATUS_WAITING_FOR_ROUTE;
						}
						$thePacket->processingRound = $gameSettings->currentDataPacketProcessingRound + 1;
						$thePacket->save();
					}
				}
			} else {
				// Debugging
				if (!$isUnitTest) echo "Datenpaket mit sourceId {$thePacket->sourceId} und destinationId {$thePacket->destinationId} in Wartestellung setzen.\n";
				
				$thePacket->status = AODV_DATA_PACKET_STATUS_NODE_BUSY;
				$thePacket->processingRound = $gameSettings->currentDataPacketProcessingRound + 1;
				$thePacket->save();
			}
			$packetCount++;
		}
	}
	
	echo "\n";
	
	$gameSettings->currentDataPacketProcessingRound++;
	$gameSettings->save();
	
	$conn->commit();
?>