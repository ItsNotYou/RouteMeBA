<?php
	include "../../init.php";

	// Metadaten

	if ($gameSettings->protocol == "aodv") {
		$messageTable = "AODVDataPacket";
	}

	$markerArray = array("messageMarkers" => array(), "playerMarkers" => array());

	// Daten für Spielermarker zusammenstellen
	
	$nodes = Doctrine_Query::create()->from("Player")->where("role = ? AND battery > ?", array(PLAYER_ROLE_NODE, 0))->orderBy("id asc")->execute();
	
	foreach ($nodes as $theNode) {
		$jsonMarker["latitude"] = $theNode->latitude;
		$jsonMarker["longitude"] = $theNode->longitude;
		$jsonMarker["id"] = $theNode->id;
		$jsonMarker["name"] = $theNode->name;
		$jsonMarker["range"] = $theNode->getRange();
		$jsonMarker["battery"] = $theNode->battery;
		if ($gameSettings->protocol == "aodv") {
			$jsonMarker["packetCount"] = Doctrine_Query::create()->from($messageTable)->where("currentNodeId = ? AND status != ?", array($theNode->id, AODV_DATA_PACKET_STATUS_ARRIVED))->execute()->count();
			
		}
		
		$markerArray["playerMarkers"][$theNode->id] = $jsonMarker;
		unset($jsonMarker);
	}
	
	// Daten für Nachrichtenmarker zusammenstelle

	$messages = Doctrine_Query::create()->from($messageTable)->where("ownerId = ?", $_REQUEST["playerId"])->execute();
	
	foreach ($messages as $theMessage) {
		$jsonMarker["ownerId"] = $theMessage->ownerId;
		$jsonMarker["latitude"] = $theMessage->CurrentNode->latitude;
		$jsonMarker["longitude"] = $theMessage->CurrentNode->longitude;
		$jsonMarker["id"] = $theMessage->id;
		$jsonMarker["status"] = $theMessage->status;
		
		$markerArray["messageMarkers"][$theMessage->id] = $jsonMarker;
		unset($jsonMarker);
	}
	
	echo json_encode($markerArray);
?>