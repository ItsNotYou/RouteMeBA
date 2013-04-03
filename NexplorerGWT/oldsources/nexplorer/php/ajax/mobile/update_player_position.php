<?php
	include "../../init.php";

	// Neue Spielerposition eintragen

	$thePlayer = Doctrine::getTable("Player")->find($_POST["playerId"]);
	$thePlayer->latitude = $_POST["latitude"];
	$thePlayer->longitude = $_POST["longitude"];
	$thePlayer->lastPositionUpdate = time();
	$thePlayer->save();
	
	// Wenn leichtester Schwierigkeitsgrad, Nachbarschaft aktualisieren
	
	if ($gameSettings->difficulty == GAME_DIFFICULTY_EASY) {
		$thePlayer->updateNeighbourhood();
	}
	
	$conn->commit();
?>