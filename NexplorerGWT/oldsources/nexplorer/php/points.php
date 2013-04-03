<?php
	include "./init.php";
	
	$packet = new AODVDataPacket;
	
	for ($i=1; $i < 10; $i++) { 
		$packet->hopsDone = $i;
		echo $packet->calculatePoints();
		echo "<br/>";
	}
?>