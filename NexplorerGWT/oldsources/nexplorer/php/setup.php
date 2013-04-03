<?php
	// require the base Doctrine class
	require_once('./lib/Doctrine.php');

	// register the autoloader
	spl_autoload_register(array('Doctrine','autoload'));
	spl_autoload_register(array('Doctrine', 'modelsAutoload')); 

	// set up a connection
	$conn = Doctrine_Manager::connection('mysql://root:opr04x@localhost/diplom');
	$conn->setCharset("utf8");

	//Doctrine::dropDatabases(array('diplom'));
	//Doctrine::createDatabases(array('diplom'));

	try {
		Doctrine::generateModelsFromYaml("./../etc/schema", "./classes/models");
	} catch (Exception $e) {	
		echo $e->getMessage();
		print_r($e);
	}

	try {
		Doctrine::createTablesFromModels("./classes/models");
	} catch (Exception $e) {
		echo $e->getMessage();
		print_r($e);
	}
?> 
