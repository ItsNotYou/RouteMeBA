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
	$gameSettings->save();
	
	$firstNode = new Player;
	$firstNode->name = "firstNode";
	$firstNode->role = PLAYER_ROLE_NODE;
	$firstNode->latitude = 48.89364;
	$firstNode->longitude = 2.33739;
	$firstNode->battery = 100;
	$firstNode->sequenceNumber = 1;
	$firstNode->hasSignalRangeBooster = time() - 60000; // ist 60 Sekunden aktiv gewesen
	$firstNode->save();
	
	$conn->flush();
	
	// Test
	
	include "../ajax/admin/get_player_stats.php";
	
	if ($firstNode->hasSignalRangeBooster == 0) {
		echo "Test erfolgreich!";
	} else {
		echo "Test fehlgeschlagen!";
	}	
?>