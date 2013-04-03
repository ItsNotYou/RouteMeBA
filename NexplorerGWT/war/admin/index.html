<?php echo '<?xml version="1.0" encoding="UTF-8"?>' ?>
<?php include "../php/init.php"; ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
	"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
	<head>
		<title>Network Explorer - Administration</title>
		
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
		
		<link rel="stylesheet" href="../css/layout.css" type="text/css" />
		<link rel="stylesheet" type="text/css" href="../css/cupertino/jquery-ui-1.8.12.custom.css">
			
		<script type="text/javascript" charset="utf-8" src="../js/lib/jquery-1.6.min.js"></script>
		<script type="text/javascript" charset="utf-8" src="../js/lib/jquery-ui-1.8.10.custom.min.js"></script>
		<script type="text/javascript" charset="utf-8" src="../js/lib/jquery.form.js"></script>
		<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
		<script type="text/javascript" charset="utf-8" src="../js/functions_admin.js"></script>
	
		<script type="text/javascript" charset="utf-8">
			$(function() {
				$("button").button();
				
				$("#startGameForm").ajaxForm({
					success: function() {
						window.location.reload();
					}
				});
			})
		</script>
	</head>

	<body>
		<?php if (!empty($gameSettings)) { ?>
			<?php $centerLatLong = $gameSettings->getPlayingFieldCenterLatLong(); ?>
			<script type="text/javascript" charset="utf-8">
				$(function() {
					getGameStats();
					initializeAdminMap(<?php echo $centerLatLong["latitude"] ?>, <?php echo $centerLatLong["longitude"] ?>, <?php echo $gameSettings->playingFieldUpperLeftLatitude ?>, <?php echo $gameSettings->playingFieldUpperLeftLongitude ?>, <?php echo $gameSettings->playingFieldLowerRightLatitude ?>, <?php echo $gameSettings->playingFieldLowerRightLongitude ?>);
				});
			</script>
			<div id="gameStats"></div>
			<br/>
			<?php if ($gameSettings->isRunning): ?>
				<script type="text/javascript" charset="utf-8">
					$(function() {
						getPlayerStats();
						updateRemainingPlayingTime();
						placeNewItems();
						updateBonusGoals();
						updateNodeBatteries();
						
						<?php 
							if ($gameSettings->protocol == "aodv") {
								echo "aodvProcessRoutingMessages();";
								echo "aodvProcessDataPackets();";
							}
						?>
					});
				</script>
				<button class="ui-state-highlight" type="button" onclick="stopGame()">Spiel unterbrechen</button>
			<?php else: ?>
				<script type="text/javascript" charset="utf-8">
					getPlayerStats();
					placeNewItems();
				</script>
				<button type="button" onclick="resumeGame()">Spiel fortsetzen</button> <button class="ui-state-error" type="button" onclick="resetGame()">Spiel beenden</button>
			<?php endif ?>
			<br/><br/>
			<div id="map_canvas" style="width:1000px; height:500px; margin: 20px auto"></div>
			<div id="playerStats"></div>
			<br/>
			<div id="itemStats"></div>
			<div id="log" class="ui-accordion ui-widget ui-widget-content ui-corner-all ui-accordion-content-active" style="border:1px solid black; text-align:center; display:none">
				<textarea id="logText" style="width: 90%; height: 400px">
					
				</textarea>
			</div>
		<?php } else { ?>
			<form id="startGameForm" action="../php/ajax/admin/start_game.php" method="POST">
				<strong>Einstellungen:</strong><br/>
				<label>Spieldauer:</label>
				<input class="ui-corner-all" type="text" name="playingTime" value="5" /> Minuten<br/>
				<label>Basisreichweite Knoten:</label>
				<input class="ui-corner-all" type="text" name="baseNodeRange" value="9" /> Meter<br/>
				<label>Reichweite "Gegenstände einsammeln":</label>
				<input class="ui-corner-all" type="text" name="itemCollectionRange" value="4" /> Meter<br/>
				<label>Max. Anzahl Batterien:</label>
				<input class="ui-corner-all" type="text" name="maxBatteries" value="5" /><br/>
				<label>Max. Anzahl Booster:</label>
				<input class="ui-corner-all" type="text" name="maxBoosters" value="2" /><br/>
				<label>Schwierigkeitsgrad:</label>
				<select name="difficulty">
					<option value="1">einfach (geringe Anforderungen / starke Assistenz)</option>
				</select><br/>
				<label>Routing-Protokoll</label>
				<select name="protocol">
					<option value="aodv">Ad-hoc On-Demand Distance Vector (AODV)</option>
				</select><br/>
				<strong>Erweiterte Einstellungen:</strong><br/>
				<label>Spielfeld obere linke Ecke:</label>
				<input class="ui-corner-all" type="text" name="playingFieldUpperLeftLatitude" value="52.39358362957616" style="width:130px" />;<input class="ui-corner-all" type="text" name="playingFieldUpperLeftLongitude" value="13.130382746458054" style="width:130px" /><br/>
				<label>Spielfeld untere rechte Ecke:</label>
				<input class="ui-corner-all" type="text" name="playingFieldLowerRightLatitude" value="52.39328409876577" style="width:130px" />;<input class="ui-corner-all" type="text" name="playingFieldLowerRightLongitude" value="13.130974173545837" style="width:130px" /><br/>
				<strong>Intervallzeiten *:</strong><br/>
				<label>Senden der Knotenposition an Server:</label>
				<input class="ui-corner-all" type="text" name="updatePositionIntervalTime" value="3000" /> Millisekunden<br/>
				<label>Aktualisierung der Benutzeroberfläche:</label>
				<input class="ui-corner-all" type="text" name="updateDisplayIntervalTime" value="3000" /> Millisekunden<br/>
				<button type="submit">Spiel starten</button>
				<div style="margin-top:20px">
					* Achtung! Intervallzeiten geben an mit welcher Frequenz versucht wird Anfragen an den Server zu senden. Anfragen können verworfen werden, wenn der Server oder die Netzwerkverbindung ausgelastet sind.
				</div>
			</form>
		<?php } ?>
	</body>
</html>