<?php
	include "../../init.php";

	$nodes = Doctrine_Query::create()->from("Player")->where("role = ? AND battery > ?", array(PLAYER_ROLE_NODE, 0))->orderBy("id asc")->execute();
	
	$markerArray = array();
	
	foreach ($nodes as $theNode) {
		$jsonMarker["latitude"] = $theNode->latitude;
		$jsonMarker["longitude"] = $theNode->longitude;
		$jsonMarker["id"] = $theNode->id;
		$jsonMarker["name"] = $theNode->name;
		$jsonMarker["range"] = $theNode->getRange();
		$jsonMarker["battery"] = $theNode->battery;
		if ($gameSettings->protocol == "aodv") {
			$jsonMarker["packetCount"] = Doctrine_Query::create()->from("AODVDataPacket")->where("currentNodeId = ? AND status != ?", array($theNode->id, AODV_DATA_PACKET_STATUS_ARRIVED))->execute()->count();
			
		}
		
		$markerArray[$theNode->id] = $jsonMarker;
	}
	
	echo json_encode($markerArray);
?>