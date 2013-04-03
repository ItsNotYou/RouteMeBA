<?php
	include "../../init.php";
	
	// Quelle: http://www.php.net/manual/en/function.mt-rand.php#75793
	function random_float ($min,$max) {
	   return ($min+lcg_value()*(abs($max-$min)));
	}
	
	function getLatLongForNewItem($gameSettings) {
		$latitude = random_float($gameSettings->playingFieldLowerRightLatitude, $gameSettings->playingFieldUpperLeftLatitude);
		$longitude = random_float($gameSettings->playingFieldUpperLeftLongitude, $gameSettings->playingFieldLowerRightLongitude);
		
		$query = Doctrine_Query::create();
		$query->select("i.*");
		$query->addSelect("( 6371 * acos( cos( radians({$latitude}) ) * cos( radians( i.latitude ) ) * cos( radians( i.longitude ) - radians({$longitude}) ) + sin( radians({$latitude}) ) * sin( radians( i.latitude ) ) ) ) distance");
		$query->from("Item i");
		$query->having("distance <= ".$gameSettings->itemCollectionRange / 1000);
		$query->orderBy("distance");
		
		echo $query->count();
		
		// Prüfe ob anderer Gegenstand zu nah an diesen Koordinaten ist
		if ($query->count() > 0) {
			$latLong = getLatLongForNewItem($gameSettings);
		} else {
			$latLong = array($latitude, $longitude);
		}
		
		return $latLong;
	}
	
	$batteries = Doctrine_Query::create()->from("Item")->where("type = ?", 1)->execute();
	if ($batteries->count() < $gameSettings->maxBatteries) {
		for($i = 1; $i <= $gameSettings->maxBatteries - $batteries->count(); $i++) {			
			$latLong = getLatLongForNewItem($gameSettings);
			
			$item = new Item;
			$item->type = 1;
			$item->latitude = $latLong[0];
			$item->longitude = $latLong[1];
			$item->created = time();
			$item->save();
		}
	}
	
	$boosters = Doctrine_Query::create()->from("Item")->where("type = ?", 2)->execute();
	if ($boosters->count() < $gameSettings->maxBoosters) {
		for($i = 1; $i <= $gameSettings->maxBoosters - $boosters->count(); $i++) {
			$latLong = getLatLongForNewItem($gameSettings);
			
			$item = new Item;
			$item->type = 2;
			$item->latitude = $latLong[0];
			$item->longitude = $latLong[1];
			$item->created = time();
			$item->save();
		}
	}
	
	$conn->commit();
?>