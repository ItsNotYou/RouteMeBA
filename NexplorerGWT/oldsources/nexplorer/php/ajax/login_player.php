<?php
	if (file_exists("../init.php")) {
		include_once "../init.php";
	}
	
	if ($_POST["isMobile"] == "true") $role = PLAYER_ROLE_NODE; else $role = PLAYER_ROLE_MESSAGE; 
	
	$thePlayer = Doctrine_Query::create()->from("Player")->where("name = ? AND role = ?", array($_POST["name"], $role))->execute()->getFirst();
	
	if (empty($thePlayer)) {
		// Algemeine Spielerdaten
		$thePlayer = new Player;
		$thePlayer->role = $role;
		$thePlayer->name = $_POST["name"];
		$thePlayer->battery = 100;
		$thePlayer->score = 0;
		
		if ($role == PLAYER_ROLE_NODE) {
			$thePlayer->latitude = $gameSettings->playingFieldUpperLeftLatitude;
			$thePlayer->longitude = $gameSettings->playingFieldUpperLeftLongitude;
		}
		
		$thePlayer->save();
	}
	
	if ($isUnitTest) {
		$returnId = $thePlayer->id;
	} else {
		echo $thePlayer->id;
	}
	
	$conn->commit();
?>