<?php
	ini_set("memory_limit","32M");

	// require the base Doctrine class
	require_once("/Applications/xampp/htdocs/diplom/php/lib/Doctrine.php");

	// register the autoloader
	spl_autoload_register(array('Doctrine','autoload'));
	spl_autoload_register(array('Doctrine', 'modelsAutoload')); 

	// set up a connection
	$conn = Doctrine_Manager::connection('mysql://root:opr04x@localhost/diplom');
	$conn->setCharset("utf8");
	
	$manager = Doctrine_Manager::getInstance();
	$manager->setAttribute(Doctrine_Core::ATTR_MODEL_LOADING, Doctrine_Core::MODEL_LOADING_CONSERVATIVE);
	
	Doctrine_Manager::getInstance()->setAttribute('model_loading', 'conservative');

	Doctrine::loadModels("/Applications/xampp/htdocs/diplom/php/classes/models");
	
	$conn->beginTransaction();
	
	for ($i=1; $i <= 4; $i++) { 
		$messages = Doctrine_Query::create()->from("Message")->where("status = ? OR status = ? OR status = ?", array(1, 3, 4))->execute();
		
		foreach ($messages as $theMessage) {
			$availableDestination = Doctrine_Query::create()->from("AvailableDestination")->where("sourceId = ? AND destinationId = ?", array($theMessage->currentNodeId, $theMessage->destinationId))->execute()->getFirst();
		
			if (!empty($availableDestination)) {
				// Testen ob Knoten noch in Reichweite sind
				if ($theMessage->CurrentNode->calculateDistanceWithPlayer($availableDestination->NextHop) < 0.024) {
					// Nachricht bewegen
					echo "Sende Nachricht {$theMessage->id} an Knoten {$availableDestination->nextHopId}.\n";

					$theMessage->currentNodeId = $availableDestination->nextHopId;
					$theMessage->hopsDone++;

					if ($theMessage->currentNodeId == $theMessage->destinationId) {
						echo "Nachricht {$theMessage->id} hat Zielknoten {$theMessage->destinationId} erreicht.\n";
						$theMessage->status = 2;
					} else {
						$theMessage->status = 1;
					}
				} else {
					echo "Nachrichte {$theMessage->id} konnte nicht an Knoten {$availableDestination->nextHopId} übermittelt werden.\n";
					$theMessage->status = 4;
				}
			} else {
				// Weg zum Ziel nicht mehr verfügbar
				echo "Kein Weg zum Ziel für Nachricht {$theMessage->id} gefunden.\n";
				$theMessage->status = 3;
			}
			
			$theMessage->save();
			$theMessage->refresh(true);
		}
		
		$conn->commit();
		sleep(15);
		$conn->beginTransaction();
	}
	
	$conn->commit();
?>