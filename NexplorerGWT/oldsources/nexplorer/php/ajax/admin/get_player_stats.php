<?php if (file_exists("../../init.php")) include "../../init.php"; ?>
<table class="ui-accordion ui-widget ui-widget-content ui-corner-all ui-accordion-content-active" style="border:1px solid black; text-align:center">
	<th colspan="8">Spieler (Nachrichten)</th>
	<tr>
		<td>ID</td>
		<td>Name</td>
		<td>Punktzahl</td>
	</tr>
	<?php foreach(Doctrine_Query::create()->from("Player")->where("role = ?", PLAYER_ROLE_MESSAGE)->execute() as $thePlayer) { ?>
		<tr>
			<td><?php echo $thePlayer->id ?></td>
			<td><?php echo $thePlayer->name ?></td>
			<td><?php echo $thePlayer->score ?></td>
		</tr>
	<?php } ?>
</table>
<br/>
<table class="ui-accordion ui-widget ui-widget-content ui-corner-all ui-accordion-content-active" style="border:1px solid black; text-align:center">
	<th colspan="8">Spieler (Knoten)</th>
	<tr>
		<td>ID</td>
		<td>Name</td>
		<td>Breitengrad</td>
		<td>Längengrad</td>
		<td>Letzte Positionsaktualisierung</td>
		<td>Punktzahl</td>
		<td>Batterie</td>
		<td>Booster seit</td>
	</tr>
	<?php foreach(Doctrine_Query::create()->from("Player")->where("role = ?", PLAYER_ROLE_NODE)->orderBy("score desc")->execute() as $thePlayer) { ?>
		<?php if ($thePlayer->hasSignalRangeBooster > 0 && time() - $thePlayer->hasSignalRangeBooster > 60) { $thePlayer->hasSignalRangeBooster = 0; } ?>
		<tr>
			<td><?php echo $thePlayer->id ?></td>
			<td><?php echo $thePlayer->name ?></td>
			<td><?php echo $thePlayer->latitude ?></td>
			<td><?php echo $thePlayer->longitude ?></td>
			<td><?php echo date("d.m.Y H:i:s", $thePlayer->lastPositionUpdate) ?>
			<td><?php echo $thePlayer->score ?></td>
			<td><?php echo $thePlayer->battery ?>%</td>
			<td><?php if ($thePlayer->hasSignalRangeBooster > 0) echo date("d.m.Y H:i:s", $thePlayer->hasSignalRangeBooster); else echo "nicht aktiv"; ?></td>
		</tr>
		<?php $thePlayer->save(); ?>
	<?php } ?>
</table>
<?php $conn->commit(); ?>