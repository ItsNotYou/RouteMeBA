<?php
	include_once "../init.php";
	
	// Vorbereitung
	
	$isUnitTest = true;
	
	include "../ajax/admin/reset_game.php";
	
	$gameSettings = new Settings;
	$gameSettings->updatePositionIntervalTime = 1;
	$gameSettings->updateDisplayIntervalTime = 1;
	$gameSettings->didEnd = 0;
	$gameSettings->isRunning = 1;
	$gameSettings->protocol = "aodv";
	$gameSettings->save();
	
	$firstNode = new Player;
	$firstNode->name = "firstNode";
	$firstNode->role = PLAYER_ROLE_NODE;
	$firstNode->latitude = 48.89364;
	$firstNode->longitude = 2.33739;
	$firstNode->battery = 100;
	$firstNode->sequenceNumber = 1;
	$firstNode->save();
	
	$secondNode = new Player;
	$secondNode->name = "secondNode";
	$secondNode->role = PLAYER_ROLE_NODE;
	$secondNode->latitude = 48.89364;
	$secondNode->longitude = 2.33739;
	$secondNode->battery = 100;
	$secondNode->sequenceNumber = 1;
	$secondNode->save();
	
	$thirdNode = new Player;
	$thirdNode->name = "thirdNode";
	$thirdNode->role = PLAYER_ROLE_NODE;
	$thirdNode->latitude = 48.89364;
	$thirdNode->longitude = 2.33739;
	$thirdNode->battery = 100;
	$thirdNode->sequenceNumber = 1;
	$thirdNode->save();
	
	$firstNeighbour = new Neighbour;
	$firstNeighbour->nodeId = $firstNode->id;
	$firstNeighbour->neighbourId = $secondNode->id;
	$firstNeighbour->save();
	
	$secondNeighbour = new Neighbour;
	$secondNeighbour->nodeId = $secondNode->id;
	$secondNeighbour->neighbourId = $thirdNode->id;
	$secondNeighbour->save();
	
	$firstRoutingTableEntry = new AODVRoutingTableEntry;
	$firstRoutingTableEntry->nodeId = $firstNode->id;
	$firstRoutingTableEntry->destinationId = $thirdNode->id; 
	$firstRoutingTableEntry->nextHopId = $secondNode->id;
	$firstRoutingTableEntry->destinationSequenceNumber = 1;
	$firstRoutingTableEntry->hopCount = 2;
	$firstRoutingTableEntry->save();
	
	$secondRoutingTableEntry = new AODVRoutingTableEntry;
	$secondRoutingTableEntry->nodeId = $secondNode->id;
	$secondRoutingTableEntry->destinationId = $thirdNode->id; 
	$secondRoutingTableEntry->nextHopId = $thirdNode->id;
	$secondRoutingTableEntry->destinationSequenceNumber = 1;
	$secondRoutingTableEntry->hopCount = 1;
	$secondRoutingTableEntry->save();
	
	$firstDataPacket = new AODVDataPacket;
	$firstDataPacket->currentNodeId = $secondNode->id;
	$firstDataPacket->sourceId = $firstNode->id;
	$firstDataPacket->destinationId = $thirdNode->id;
	$firstDataPacket->hopsDone = 1;
	$firstDataPacket->status = AODV_DATA_PACKET_STATUS_UNDERWAY;
	$firstDataPacket->processingRound = 1;
	$firstDataPacket->save();
	
	$conn->flush();
	
	// Test
	
	include "../ajax/admin/aodv_process_data_packets.php";
	
	$dataPacket = Doctrine_Query::create()->from("AODVDataPacket")->where("sourceId = ? AND destinationId = ?", array($firstNode->id, $thirdNode->id))->execute()->getFirst();
	
	if ($dataPacket->status == AODV_DATA_PACKET_STATUS_ARRIVED) {
		echo "Test erfolgreich!";
	} else {
		echo "Test fehlgeschlagen!";
	}	
?>