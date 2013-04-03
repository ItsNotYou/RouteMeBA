<?php
	include "../../init.php";
	
	$items = Doctrine_Query::create()->from("Item")->execute();
	
	if ($items->count() > 0) {
		foreach ($items as $theItem) {
			$itemArray = array("type" => $theItem->type, "latitude" => $theItem->latitude, "longitude" => $theItem->longitude);
			$response["items"][$theItem->id] = $itemArray;
		}
	}
	
	print_r(json_encode($response));
?>