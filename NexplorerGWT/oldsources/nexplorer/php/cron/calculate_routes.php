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

	function calculateRoutes($startingNode, $firstBranch, $visitedNodes, $neighbors, $hopCount) {	
		//echo "hopCount {$hopCount}\n";
		$myVisitedNodes = $visitedNodes;
		foreach (!empty($neighbors) ? $neighbors : array() as $theNeighbor) {
			$existingEntry = Doctrine_Query::create()->from("AvailableDestination")->where("sourceId = ? AND destinationId = ?", array($startingNode->id, $theNeighbor->id))->execute()->getFirst();
			
			if (empty($existingEntry) || $existingEntry->hopsToDestination > $hopCount) {
				
				if (!empty($existingEntry)) {
					$existingEntry->delete();
				}
				
				$newAvailableDestination = new AvailableDestination;
				$newAvailableDestination->sourceId = $startingNode->id;
				$newAvailableDestination->destinationId = $theNeighbor->id;
				if ($hopCount == 1) {
					$firstBranch = $theNeighbor;	
				} 
				$newAvailableDestination->nextHopId = $firstBranch->id;
				$newAvailableDestination->hopsToDestination = $hopCount;
				$newAvailableDestination->save();
				
				echo "Trage neue Route von {$startingNode->id} nach {$theNeighbor->id} mit {$hopCount} Spr&uuml;ngen ein. N&auml;chster Knoten ist {$firstBranch->id}.\n";
			}
			
			$myVisitedNodes[] = $theNeighbor->id;
			
			echo "Besuchte Knoten: ";
			foreach ($visitedNodes as $theVisitedNode) {
				echo $theVisitedNode." ";
			}
			echo "\n";
			
			$newNeighbors = $theNeighbor->neighborDiscovery($visitedNodes);
			
			if (!empty($newNeighbors)) {
				calculateRoutes($startingNode, $firstBranch, $myVisitedNodes, $newNeighbors, $hopCount+1);
			} else {
				echo "Knoten {$theNeighbor->id} hat keine interessanten Nachbarn.\n";
			}
		}
		
		return true;
	}

	$dbh = Doctrine_Manager::getInstance()->getCurrentConnection()->getDbh();
	$dbh->query('TRUNCATE TABLE available_destinations');

	$nodes = Doctrine_Query::create()->from("Player")->where("role = ? OR role = ?", array(2, 3))->execute();
	
	$time = time();
	
	foreach ($nodes as $theNode) {
		$startingNode = $theNode;
		
		echo "<strong>Start Routenberechnung f&uuml;r Knoten {$startingNode->id}.</strong>\n";
		
		$neighbors = $startingNode->neighborDiscovery();
		
		calculateRoutes($startingNode, null, array($startingNode->id), $neighbors, 1);
		
		echo "<strong>Ende Routenberechnung f&uuml;r Knoten {$startingNode->id}.</strong>\n\n"; 
	}
	
	echo "Zeit f&uuml;r Berechnung ".(time()-$time)." Sekunden.";

	$conn->commit();
	
?>