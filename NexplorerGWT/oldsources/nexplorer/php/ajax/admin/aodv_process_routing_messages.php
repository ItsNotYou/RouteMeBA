<?php
	header("Content-Type: text/html; charset=utf-8");

	include "../../init.php";
	
	// alle Knoten bearbeiten welche noch im Spiel sind (zufällige Reihenfolge)
	$nodes = Doctrine_Query::create()->select("*, RANDOM() AS rand")->from("Player")->where("role = ? AND battery > ?", array(PLAYER_ROLE_NODE, 0))->orderBy("rand")->execute();
	
	echo "------------adovProcessRoutingMessages Runde ".$gameSettings->currentDataPacketProcessingRound." ".date("m.d.Y H:i:s")."------------\n";
	
	foreach($nodes as $theNode) {
		// alle RREQ des Knotens bearbeiten
		echo "***RREQs bei Knoten {$theNode->id}***\n";
		$nodeRREQs = Doctrine_Query::create()->from("AODVRoutingMessage")->where("currentNodeId = ? AND type = ? AND processingRound = ?", array($theNode->id, AODV_ROUTING_MESSAGE_TYPE_RREQ, $gameSettings->currentRoutingMessageProcessingRound))->execute();
		foreach($nodeRREQs as $theRREQ) {
			// ist TTL des RREQ abgelaufen?
			if ($theRREQ->lifespan <= 0) {
				// RREQ entfernen
				// Debugging
				echo "RREQ mit sourceId {$theRREQ->sourceId} und sequenceNumber {$theRREQ->sequenceNumber} löschen, weil TTL <= 0.\n";
				$theRREQ->delete();
				continue;
			}
			// ist RREQ schon im Puffer?
			$RREQBufferEntry = Doctrine_Query::create()->from("AODVRouteRequestBufferEntry")->where("nodeId = ? AND sourceId = ? AND sequenceNumber = ?", array($theNode->id, $theRREQ->sourceId, $theRREQ->sequenceNumber))->execute()->getFirst();
			if (empty($RREQBufferEntry)) {
				// bin ich das Ziel des RREQ?
				if ($theRREQ->destinationId == $theNode->id) {
					$theNode->createRouteForRREQ($theRREQ);
					// RREQ entfernen
					// Debugging
					echo "RREQ mit sourceId {$theRREQ->sourceId} und sequenceNumber {$theRREQ->sequenceNumber} löschen, weil ich das Ziel bin.\n";
					$theRREQ->delete();
					continue;
				}
				// kenne ich eine Route zum Ziel des RREQ?
				$theRoute = $theNode->getRouteToDestination($theRREQ->destinationId);
				if (!empty($theRoute)) {
					$theNode->createRouteForRREQFromExistingRoute($theRREQ, $theRoute);
					// RREQ entfernen
					// Debugging
					echo "RREQ mit sourceId {$theRREQ->sourceId} und sequenceNumber {$theRREQ->sequenceNumber} löschen, weil ich eine Route zum Ziel kenne.\n";
					$theRREQ->delete();
					continue;
				}	
				// RREQ an Nachbarn weitersenden
				foreach($theNode->Neighbours as $theNeighbourListEntry) {
					$theNode->forwardRouteRequestToNeighbour($theRREQ, $theNeighbourListEntry->Neighbour);
				}
				// RREQ entfernen
				// Debugging
				echo "RREQ mit sourceId {$theRREQ->sourceId} und sequenceNumber {$theRREQ->sequenceNumber} löschen, weil er fertig bearbeitet ist.\n";
				$theRREQ->delete();
			} else {
				// RREQ verwerfen
				// Debugging
				echo "RREQ mit sourceId {$theRREQ->sourceId} und sequenceNumber {$theRREQ->sequenceNumber} löschen, weil er schonmal bearbeitet wurde.\n";
				$theRREQ->delete();
			}
		}
		
		// alle RERR des Knotens bearbeiten
		echo "***RERRs bei Knoten {$theNode->id}***\n";
		$nodeRERRs = Doctrine_Query::create()->from("AODVRoutingMessage")->where("currentNodeId = ? AND type = ? AND processingRound = ?", array($theNode->id, AODV_ROUTING_MESSAGE_TYPE_RERR, $gameSettings->currentRoutingMessageProcessingRound))->execute();
		foreach($nodeRERRs as $theRERR) {
			// Prüfen ob Einträge in meiner Routingtabelle betroffen sind
			$theRoutingTableEntry = Doctrine_Query::create()->from("AODVRoutingTableEntry")->where("nodeId = ? AND nextHopId = ? AND destinationId = ?", array($theNode->id, $theRERR->sourceId, $theRERR->destinationId))->execute()->getFirst();
			if (!empty($theRoutingTableEntry)) {
				// Debugging
				echo "RERR mit sourceId {$theRERR->sourceId} und destinationId {$theRERR->destinationId} betrifft Routingtabelleneintrag mit nextHopId {$theRoutingTableEntry->nextHopId} und destinationId {$theRoutingTableEntry->destinationId}.\n";
				
				// RREQ an Nachbarn weitersenden
				foreach($theNode->Neighbours as $theNeighbourListEntry) {
					$theNode->forwardRouteErrorToNeighbour($theRERR, $theNeighbourListEntry->Neighbour);
				}
				
				// Routingtabelleneintrag löschen
				// Debugging
				echo "Lösche Routingtabelleneintrag mit mit nextHopId {$theRoutingTableEntry->nextHopId} und destinationId {$theRoutingTableEntry->destinationId}.\n";
				$theRoutingTableEntry->delete();
				
				// RERR löschen
				// Debugging
				echo "RERR mit sourceId {$theRERR->sourceId} und destinationId {$theRERR->destinationId} löschen, weil er fertig bearbeitet ist.\n";
				$theRERR->delete();
			} else {
				// RERR löschen
				// Debugging
				echo "RERR mit sourceId {$theRERR->sourceId} und destinationId {$theRERR->destinationId} löschen, weil uninteressant.\n";
				$theRERR->delete();
			}		
		}
		
		echo "\n";
	}
	
	$gameSettings->currentRoutingMessageProcessingRound++;
	$gameSettings->save();
	
	$conn->commit();
?>