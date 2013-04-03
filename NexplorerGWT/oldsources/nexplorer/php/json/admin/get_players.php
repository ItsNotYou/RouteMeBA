<?php
	include "../../init.php";
	
	$players = Doctrine_Query::create()->from("Player")->where("role = ? AND battery > ?", array(2, 0))->execute();
	
	if ($players->count() > 0) {
		foreach ($players as $thePlayer) {
			$playerArray = array("latitude" => $thePlayer->latitude, "longitude" => $thePlayer->longitude, "name" => $thePlayer->name, "range" => $thePlayer->getRange());
			$response["players"][$thePlayer->id] = $playerArray;
		}
	}
	
	print_r(json_encode($response));
?>