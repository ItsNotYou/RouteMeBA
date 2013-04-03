<?php
	include "../../init.php";

	if ($gameSettings->protocol == "aodv") {
		$messageTable = "AODVDataPacket";
	}

	$markers = Doctrine_Query::create()->from($messageTable)->where("ownerId = ?", $_REQUEST["playerId"])->execute();
	
	$markerArray = array();
	
	foreach ($markers as $theMarker) {
		$jsonMarker["ownerId"] = $theMarker->ownerId;
		$jsonMarker["latitude"] = $theMarker->CurrentNode->latitude;
		$jsonMarker["longitude"] = $theMarker->CurrentNode->longitude;
		$jsonMarker["id"] = $theMarker->id;
		$jsonMarker["status"] = $theMarker->status;
		
		$markerArray[$theMarker->id] = $jsonMarker;
	}
	
	echo json_encode($markerArray);
?>