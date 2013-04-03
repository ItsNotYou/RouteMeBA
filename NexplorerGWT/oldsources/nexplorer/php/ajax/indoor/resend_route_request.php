<?php
	include "../../init.php";

	$sourceNode = Doctrine::getTable("Player")->find($_POST['sourceNodeId']);
	$thePacket = Doctrine::getTable("AODVDataPacket")->findOneByOwnerId($_POST["playerId"]);
	
	$sourceNode->sendRouteRequestToNeighbours($_POST['destinationNodeId']);
	$thePacket->status = AODV_DATA_PACKET_STATUS_WAITING_FOR_ROUTE;
	$thePacket->save();
	
	$conn->commit();
?>