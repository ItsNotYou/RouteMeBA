<?php include "../../init.php"; ?>
<table class="ui-accordion ui-widget ui-widget-content ui-corner-all ui-accordion-content-active" style="border:1px solid black; text-align:center">
	<th colspan="7">Spielparameter</th>
	<tr>
		<td>Spielstart</td>
		<td>Spiel zuende</td>
		<td>Restliche Spielzeit</td>
		<td>Spielfeld oben links</td>
		<td>Spielfeld unten rechts</td>
		<td>Aktuelle Routingrunde</td>
		<td>Aktuelle Datenrunde</td>
	</tr>
	<tr>
		<td><?php echo date("d.m.Y H:i", $gameSettings->runningSince) ?></td>
		<td><?php echo $gameSettings->didEnd ? "ja" : "nein" ?></td>
		<td><?php echo $gameSettings->remainingPlayingTime ?></td>
		<td><?php echo $gameSettings->playingFieldUpperLeftLatitude.";".$gameSettings->playingFieldUpperLeftLongitude ?></td>
		<td><?php echo $gameSettings->playingFieldLowerRightLatitude.";".$gameSettings->playingFieldLowerRightLongitude ?></td>
		<td><?php echo $gameSettings->currentRoutingMessageProcessingRound ?></td>
		<td><?php echo $gameSettings->currentDataPacketProcessingRound ?></td>
	</tr>
</table>