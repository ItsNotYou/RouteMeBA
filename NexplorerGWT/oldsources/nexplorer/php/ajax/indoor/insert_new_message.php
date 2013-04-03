<?php
	if (file_exists("../../init.php")) {
		include_once "../../init.php";
	}

	$sourceNode = Doctrine::getTable("Player")->find($_POST['sourceNodeId']);

	if ($sourceNode->battery <= 0) { 
		if (!$isUnitTest) exit; 
	}

	if ($gameSettings->protocol == "aodv" && $sourceNode->battery > 0) {
		$newMessage = new AODVDataPacket;
		$newMessage->ownerId = $_POST['ownerId'];
		$newMessage->sourceId = $_POST['sourceNodeId'];
		$newMessage->currentNodeId = $_POST['sourceNodeId'];
		$newMessage->destinationId = $_POST['destinationNodeId'];
		$newMessage->processingRound = $gameSettings->currentDataPacketProcessingRound + 1;

		// prüfen ob Route zum Ziel bekannt
		$theRoute = $sourceNode->getRouteToDestination($_POST['destinationNodeId'], $isUnitTest);
		if (!empty($theRoute)) {
			// Nachricht auf ihren Weg schicken
			$newMessage->status = AODV_DATA_PACKET_STATUS_UNDERWAY;
		} else {
			// Nachticht in Warteschleife setzen
			$newMessage->status = AODV_DATA_PACKET_STATUS_WAITING_FOR_ROUTE;
			// Route Request an Nachbarn senden
			$sourceNode->sendRouteRequestToNeighbours($_POST['destinationNodeId'], $isUnitTest);
		}

		$newMessage->save();
	}
	
	$conn->commit();
?>