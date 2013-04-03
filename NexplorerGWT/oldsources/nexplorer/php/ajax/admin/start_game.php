<?php
	include "../../init.php";
	
	$gameSettings = new Settings;
	$gameSettings->isRunning = 0;
	$gameSettings->runningSince = time();
	$gameSettings->lastPause = time();
	$gameSettings->remainingPlayingTime = $_POST["playingTime"] * 60;
	foreach ($_POST as $key => $value) {
		 if ($key == "playingTime") {
			$gameSettings->$key = $value * 60;
		} else {
			$gameSettings->$key = $value;
		}
	}
	$gameSettings->save();
	
	$conn->commit();
?>