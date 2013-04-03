<?php
	include "../../init.php";

	$gameSettings = Doctrine_Query::create()->from("Settings")->execute()->getFirst();
	$gameSettings->isRunning = 1;
	$gameSettings->lastPause = time();
	$gameSettings->save();
	
	$conn->commit();
?>