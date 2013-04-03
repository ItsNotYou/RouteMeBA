<?php
	include "../../init.php";
	
	// Spieleinstellungen und Spielstatus
	
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
	
	// Spielerinformationen
	
	$thePlayer = Doctrine::getTable("Player")->find($_REQUEST["playerId"]);
	
	$response["score"] = $thePlayer->score;
	$response["battery"] = $thePlayer->battery;
	$response["latitude"] = $thePlayer->latitude;
	$response["longitude"] = $thePlayer->longitude;
	
	$response["range"] = $thePlayer->getRange();
	$neighbours = $thePlayer->getNeighbours();
	$response["neighbourCount"] = $neighbours->count();
	$response["neighbours"] = array();
	$items = $thePlayer->getNearbyItems();
	$response["nearbyItemsCount"] = $items->count();
	$response["nearbyItems"] = array();

	if ($neighbours->count() > 0) {
		foreach ($neighbours as $theNeighbour) {
			$neighbourArray = array("latitude" => $theNeighbour->Neighbour->latitude, "longitude" => $theNeighbour->Neighbour->longitude);
			$response["neighbours"][$theNeighbour->neighbourId] = $neighbourArray;
		}
	}

	if ($items->count() > 0) {
		foreach ($items as $theItem) {
			$itemArray = array("type" => $theItem->type, "latitude" => $theItem->latitude, "longitude" => $theItem->longitude);
			$response["nearbyItems"][$theItem->id] = $itemArray;
		}
	}

	$response["nextItemDistance"] = $thePlayer->getNearestItemDistance();
	$itemInCollectionRange = $thePlayer->getItemInCollectionRange();
	$response["itemInCollectionRange"] = (int)!empty($itemInCollectionRange);
	$response["hasRangeBooster"] = (int)($thePlayer->hasSignalRangeBooster > 0);
	
	// Hinweis zusammenstellen
	if ($thePlayer->battery < 20) {
		$response["hint"] = "Dein Akku ist ist so gut wie leer. Sammel schnell eine Batterie ein!";
	} else if ($thePlayer->battery < 50) {
		$response["hint"] = "Dein Akku ist schon halb leer. Sammel eine Batterie ein um ihn wieder aufzuladen.";
	} else if (!empty($itemInCollectionRange)) {
		$response["hint"] = "Es befindet sich ein Gegenstand in Einsammelreichweite. Schnell, sammel ihn ein!";
	} else if ($thePlayer->hasSignalRangeBooster > 0) {
		$response["hint"] = "Dein Booster wirkt noch ".(60 - (time() - $thePlayer->hasSignalRangeBooster))." Sekunden.";
	} else {
		$generalResponses = array(
			"Umso mehr Nachbarn du hast umso mehr Punkte erhältst du. Dein Akku wird allerdings auch stärker belastet.",
			"Sammel einen Booster ein um deine Sende- und Empfangsreichweite (der blaue Kreis) für 60 Sekunden zu erhöhen.",
			"Gegenstände können nur eingesammelt werden, wenn sie innerhalb des roten Kreises sind.",
			"Du suchst andere Knoten? Sieh dich einfach um!",
			"Wird eine Nachricht über dich versendet erhältst du dafür Punkte."
		);
		
		$response["hint"] = $generalResponses[mt_rand(0, count($generalResponses) - 1)];
	}
	
	print_r(json_encode($response));
?>