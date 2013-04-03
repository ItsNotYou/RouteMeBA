<?php
	include "../../init.php";

	if ($gameSettings->protocol == "aodv") {
		$playerMessage = Doctrine::getTable("AODVDataPacket")->findOneByOwnerId($_REQUEST["playerId"]);
		if($playerMessage) $playerMessage->delete();
	}

	$conn->commit();
?>