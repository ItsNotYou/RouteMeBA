<?php
	if (file_exists("../../init.php")) {
		include_once "../../init.php";
	}

	$dbh = Doctrine_Manager::getInstance()->getCurrentConnection()->getDbh();
	$dbh->query('TRUNCATE TABLE aodv_data_packets');
	$dbh->query('TRUNCATE TABLE aodv_node_data');
	$dbh->query('TRUNCATE TABLE aodv_routing_messages');
	$dbh->query('TRUNCATE TABLE aodv_routing_table_entries');
	$dbh->query('TRUNCATE TABLE aodv_route_request_buffer_entries');
	$dbh->query('TRUNCATE TABLE items');
	$dbh->query('TRUNCATE TABLE neighbours');
	$dbh->query('TRUNCATE TABLE players');
	$dbh->query('TRUNCATE TABLE settings');
	
	if (!$isUnitTest) {
		$conn->commit();
	} else {
		$conn->flush();
	}
?>