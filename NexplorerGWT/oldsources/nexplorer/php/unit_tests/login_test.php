<?php
	include_once "../init.php";
	
	// Vorbereitung
	
	$isUnitTest = true;
	
	include "../ajax/admin/reset_game.php";
	
	$_POST["isMobile"] = "true";
	$_POST["name"] = "Max Power";
	$returnId = 0;
	$playerFieldNames = Doctrine_Core::getTable("Player")->getFieldNames();
	
	// Test
	
	include "../ajax/login_player.php";
	
	if ($returnId > 0) {
		echo "Test erfolgreich!";
	} else {
		echo "Test fehlgeschlagen!";
	}
	
	//echo "<br/><br/>";
	
	//echo "**Daten des angemeldeten Spielers**<br/>";
	//foreach ($playerFieldNames as $theFieldName) {
		//echo $theFieldName.": ".$thePlayer->$theFieldName."<br/>";
	//}
?>