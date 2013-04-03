<?php
	ini_set("memory_limit", "256M");

	require_once($_SERVER['DOCUMENT_ROOT'].'/nexplorer/php/lib/Doctrine.php');
	spl_autoload_register(array('Doctrine', 'autoload'));

	Doctrine_Core::compile('Doctrine.compiled.php', array('mysql'));
?>