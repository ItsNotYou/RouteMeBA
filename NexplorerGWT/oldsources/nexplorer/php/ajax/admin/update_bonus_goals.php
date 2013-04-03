<?php
	include "../../init.php";

	$gameSettings->findNewBonusGoal();
		
	$conn->commit();
?>