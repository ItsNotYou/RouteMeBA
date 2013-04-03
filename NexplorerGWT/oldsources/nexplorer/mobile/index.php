<?php echo '<?xml version="1.0" encoding="UTF-8"?>' ?>
<?php include "../php/init.php"; ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
	"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
	<head>
		<title>Network Explorer - Benutzeroberfläche "Knoten"</title>
		
		<link rel="stylesheet" href="../js/lib/sencha-touch/resources/css/sencha-touch.css" type="text/css" />
	
		<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true"></script>
		<script type="text/javascript" charset="utf-8" src="../js/lib/jquery-1.6.min.js"></script>
		<script type="text/javascript" charset="utf-8" src="../js/lib/jquery-ui-1.8.10.custom.min.js"></script>
		<script type="text/javascript" charset="utf-8" src="../js/lib/sencha-touch/sencha-touch.js"></script>
		<script type="text/javascript" charset="utf-8" src="../js/lib/sencha-touch/examples/map/plugins/GmapTracker.js"></script>
		<script type="text/javascript" charset="utf-8" src="../js/consts.js"></script>
		<script type="text/javascript" charset="utf-8" src="../js/functions_mobile.js"></script>
	
		<link rel="stylesheet" type="text/css" href="../css/cupertino/jquery-ui-1.8.12.custom.css">
	</head>

	<body>
		<div style="display: none;">
		    <div id="login" style="text-align:center">
				Bitte geben Sie ihren gewünschten Spielernamen ein. Wenn sie ein unterbrochenes Spiel wieder aufnehmen wollen, benutzen Sie bitte den gleichen Namen wie zuvor.
				<br/><br/>
				<input id="playerName" type="text" />
				<br/><br/>
				<button id="loginButton" type="button" onclick="loginPlayer($('#playerName').val(), true)" onkeypress="loginCheck(event)">anmelden</button>
			</div>
			
			<div id="waiting" style="text-align:center">
				<span id="waitingText">
					Warte auf Spielstart.
				</span>
			</div>
			
			<div id="noPosition" style="text-align:center">
				<span>
					Bitte warten. Position konnte nicht ermittelt werden.
				</span>
			</div>
		</div>
	</body>
</html>