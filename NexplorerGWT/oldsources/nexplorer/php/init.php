<?php
	$isUnitTest = false;
	$itemNames = array(1 => "Batterie", 2 => "Booster");

	// Konstanten laden
	require_once($_SERVER['DOCUMENT_ROOT'].'/nexplorer/php/const.php');
	// require the base Doctrine class
	require_once($_SERVER['DOCUMENT_ROOT'].'/nexplorer/php/lib/Doctrine.php');

	// register the autoloader
	spl_autoload_register(array('Doctrine','autoload'));
	spl_autoload_register(array('Doctrine', 'modelsAutoload')); 

	// set up a connection
	$conn = Doctrine_Manager::connection('mysql://root:password@localhost/diplom');
	$conn->setCharset("utf8");
	
	$manager = Doctrine_Manager::getInstance();
	$manager->setAttribute(Doctrine_Core::ATTR_MODEL_LOADING, Doctrine_Core::MODEL_LOADING_CONSERVATIVE);
	
	Doctrine_Manager::getInstance()->setAttribute('model_loading', 'conservative');

	Doctrine::loadModels($_SERVER['DOCUMENT_ROOT']."/nexplorer/php/classes/models");
	
	$conn->beginTransaction();

	$gameSettings = Doctrine_Query::create()->from("Settings")->execute()->getFirst();
?>