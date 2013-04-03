<?php
	// Rollen
	define("PLAYER_ROLE_MESSAGE", 1);
	define("PLAYER_ROLE_NODE", 2);

	// AODV Routingnachrichten
	define("AODV_ROUTING_MESSAGE_TYPE_RREQ", 1);
	define("AODV_ROUTING_MESSAGE_TYPE_RERR", 2);
	
	// AODV Datenpaketstatus
	define("AODV_DATA_PACKET_STATUS_UNDERWAY", 1);
	define("AODV_DATA_PACKET_STATUS_ARRIVED", 2);
	define("AODV_DATA_PACKET_STATUS_ERROR", 3);
	define("AODV_DATA_PACKET_STATUS_WAITING_FOR_ROUTE", 4);
	define("AODV_DATA_PACKET_STATUS_NODE_BUSY", 5);
	
	// Schwierigkeitsgrade
	
	define("GAME_DIFFICULTY_EASY", 1);
	define("GAME_DIFFICULTY_MEDIUM", 2);
	define("GAME_DIFFICULTY_HARD", 3);
?>