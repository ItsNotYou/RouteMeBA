<?php echo '<?xml version="1.0" encoding="UTF-8"?>' ?>
<?php include "../php/init.php"; ?>
<?php
	$legendItems = array("network-wireless-small.png" => "Spieler / Knoten",
						"network-status.png" => "Knoten (geringe Auslastung)",
						"network-status-away.png" => "Knoten (mittlere Auslastung)",
						"network-status-busy.png" => "Knoten (starke Auslastung)",
						"mail.png" => "Nachricht unterwegs",
						//"mail--arrow.png" => "Nachricht mit hoher Priorität",
						"mail--tick.png" => "Nachricht am Ziel",
						"mail--exclamation.png" => "Wartet auf Wegfindung",
						"mail--slash.png" => "Fehler bei der Routensuche",
						"mail--clock.png" => "Wartet weil Knoten beschäftigt",
						"flag-white.png" => "Startknoten",
						"flag-black.png" => "Zielknoten",
						"star.png" => "Bonusziel");
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
	"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<title>Network Explorer - Benutzeroberfläche "Nachrichten"</title>
	
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
		
	<script type="text/javascript" charset="utf-8" src="../js/lib/jquery-1.6.min.js"></script>
	<script type="text/javascript" charset="utf-8" src="../js/lib/jquery-ui-1.8.10.custom.min.js"></script>
	<script type="text/javascript" charset="utf-8" src="../js/lib/jquery.simplemodal.js"></script>
	<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
	<script type="text/javascript" charset="utf-8" src="../js/functions_indoor.js"></script>
	
	<link rel="stylesheet" type="text/css" href="../css/south-street/jquery-ui-1.8.10.custom.css">
	<link rel="stylesheet" type="text/css" href="../css/basic.css">
	
	<script type="text/javascript" charset="utf-8">
		$(function() {
			updateGameStatus(false);
			
			$("#loginModal").modal({
				onOpen: function (dialog) {
					dialog.overlay.fadeIn('slow', function () {
						dialog.data.show();
						dialog.container.fadeIn('slow');
					})
				},
				maxHeight: 300, 
				maxWidth: 300
			});
			
			startGameStatusInterval();
			
			$("button").button();
		});
	</script>
</head>

<?php if (!empty($_GET["playerId"])) $currentPlayer = Doctrine::getTable("Player")->find($_GET["playerId"]); ?>

<body>
	<div style="position:relative">
		<div id="map_canvas" style="width:800px; height:600px; margin: 0 auto; float:left; margin-left:10px"></div>
		<div id="playerInfo" class="ui-accordion ui-widget ui-widget-content ui-corner-bottom ui-accordion-content-active" style="float:left; margin-left: 20px; width: 300px; padding: 5px; font-size: 14px"></div>
		<div class="ui-accordion ui-widget ui-widget-content ui-corner-bottom ui-accordion-content-active" style="float:left; margin-left: 20px; margin-top:10px; width: 300px; padding: 5px; font-size: 14px">
			<div style="width:100%; text-align:center; font-weight:bold">***Legende***</div>
			<table style="width:100%">
				<?php foreach ($legendItems as $key => $value): ?>
					<tr>
						<td style="text-align:center">
							<img src="../media/images/icons/<?php echo $key ?>" style="vertical-align: middle" />
						</td>
						<td>
							<?php echo $value ?>
						</td>
					</tr>
				<?php endforeach ?>
			</table>
		</div>
	</div>
	
	<div class="ui-widget" id="loginModal" style="text-align:center; display:none">
		<span id="loginReady" style="display:none">
			Bitte geben Sie ihren gewünschten Spielernamen ein. Wenn sie ein unterbrochenes Spiel wieder aufnehmen wollen, benutzen Sie bitte den gleichen Namen wie zuvor.
			<br/><br/>
			<input id="playerName" type="text" />
			<br/><br/>
			<button id="loginButton" type="button" onclick="loginPlayer($('#playerName').val(), false)">anmelden</button>
		</span>
		<span id="loginNotReady">
			Es wurde noch kein Spiel gestartet. Bitte warten Sie auf Anweisungen des Spielleiters.
		</span>
	</div>
	
	<div class="ui-widget" id="waitingOverlay" style="text-align:center; display:none">
		<span id="waitingText"></span>
	</div>
</body>
</html>
