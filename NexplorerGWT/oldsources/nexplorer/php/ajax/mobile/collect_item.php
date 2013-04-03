<?php
	include "../../init.php";

	$thePlayer = Doctrine::getTable("Player")->find($_POST["playerId"]);
	$theItem = $thePlayer->getItemInCollectionRange();
	
	if (!empty($theItem)) {
		if ($theItem->type == 1) {
			$thePlayer->battery = min(array($thePlayer->battery + 20, 100));
		} else if ($theItem->type == 2) {
			$thePlayer->hasSignalRangeBooster = time();
		}
		$theItem->delete();
		$thePlayer->save();
	}
	
	$conn->commit();
?>