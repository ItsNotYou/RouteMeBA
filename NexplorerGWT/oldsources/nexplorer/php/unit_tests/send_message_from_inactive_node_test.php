<?php
	include_once "../init.php";
	
	// Vorbereitung
	
	$isUnitTest = true;
	
	include "../ajax/admin/reset_game.php";
	
	$theSourceNode = new Player;
	$theSourceNode->role = PLAYER_ROLE_NODE;
	$theSourceNode->battery = 0;
	$theSourceNode->save();
	
	$conn->flush();
	
	$_POST['sourceNodeId'] = $theSourceNode->id;
	
	// Test
	
	include "../ajax/indoor/insert_new_message.php";

	if(empty($newMessage)) {
		echo "Test erfolgreich!";
	} else {
		echo "Test fehlgeschlagen!";
	}
?>