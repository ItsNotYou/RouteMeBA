<?php
	include "../../init.php";

	$gameSettings = Doctrine_Query::create()->from("Settings")->execute()->getFirst();
	$gameSettings->remainingPlayingTime = MAX(0, $gameSettings->remainingPlayingTime - (time() - $gameSettings->lastPause));
	$gameSettings->lastPause = time();
	
	// Spielende wenn Zeit abgelaufen
	if ($gameSettings->remainingPlayingTime == 0) {
		$gameSettings->didEnd = 1;
	}
	
	$gameSettings->save();
	
	$conn->commit();
?>