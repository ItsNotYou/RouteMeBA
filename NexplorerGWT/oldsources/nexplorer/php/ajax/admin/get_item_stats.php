<?php include "../../init.php"; ?>
<table class="ui-accordion ui-widget ui-widget-content ui-corner-all ui-accordion-content-active" style="border:1px solid black; text-align:center">
	<th colspan="5">Gegenstände</th>
	<tr>
		<td>ID</td>
		<td>Typ</td>
		<td>Breitengrad</td>
		<td>Längengrad</td>
		<td>Erstellt am</td>
	</tr>
	<?php foreach(Doctrine_Query::create()->from("Item")->execute() as $theItem) { ?>
		<tr>
			<td><?php echo $theItem->id ?></td>
			<td><?php echo $itemNames[$theItem->type] ?></td>
			<td><?php echo $theItem->latitude ?></td>
			<td><?php echo $theItem->longitude ?></td>
			<td><?php echo date("d.m.Y H:i:s", $theItem->created) ?>
		</tr>
	<?php } ?>
</table>