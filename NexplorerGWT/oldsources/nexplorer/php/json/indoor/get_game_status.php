<?php
	include "../../init.php";
	
	if (empty($gameSettings)) {
		$response["gameExists"] = 0;
		$response["isRunning"] = 0;
		$response["remainingPlayingTime"] = 0;
		$response["baseNodeRange"] = 0;
		$response["gameDifficulty"] = 0;
		$response["itemCollectionRange"] = 0;
		$response["playingFieldCenterLatitude"] = 0;
		$response["playingFieldCenterLongitude"] = 0;
		$response["bonusGoal"] = 0;
		$response["didEnd"] = 0;
		$response["updatePositionIntervalTime"] = 3000;
		$response["updateDisplayIntervalTime"] = 3000;
	} else {
		$response["gameExists"] = 1;
		$response["isRunning"] = $gameSettings->isRunning;
		$response["remainingPlayingTime"] = $gameSettings->remainingPlayingTime;
		$response["baseNodeRange"] = $gameSettings->baseNodeRange;
		$response["gameDifficulty"] = $gameSettings->difficulty;
		$response["itemCollectionRange"] = $gameSettings->itemCollectionRange;
		$playingFieldCenter = $gameSettings->getPlayingFieldCenterLatLong();
		$response["playingFieldCenterLatitude"] = $playingFieldCenter["latitude"];
		$response["playingFieldCenterLongitude"] = $playingFieldCenter["longitude"];
		$response["bonusGoal"] = $gameSettings->bonusGoal;
		$response["didEnd"] = $gameSettings->didEnd;
		$response["updatePositionIntervalTime"] = $gameSettings->updatePositionIntervalTime;
		$response["updateDisplayIntervalTime"] = $gameSettings->updateDisplayIntervalTime;
	}
	
	print_r(json_encode($response));
?>