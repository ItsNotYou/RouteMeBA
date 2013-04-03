<?php
	if (file_exists("../../init.php")) {
		include "../../init.php";
	}
	
	$currentPlayer = Doctrine::getTable("Player")->find($_REQUEST["playerId"]);
	
	$currentPlayer->updateNeighbourhood($isUnitTest);
	
	$conn->commit();
?>