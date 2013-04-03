<?php include "../../init.php" ?>
<?php $currentPlayer = Doctrine::getTable("Player")->find($_POST["playerId"]) ?>

<div style="width:100%; text-align:center; font-weight:bold">***Spielstatus***</div>
<table style="width:100%">
	<tr>
		<td>
			<img src="../media/images/icons/clock-select-remain.png" style="vertical-align: middle" />
		</td>
		<td>
			Verbleibende Spielzeit
		</td>
		<td>
			<?php echo $gameSettings->remainingPlayingTime ?> Sekunden
		</td>
	</tr>
</table>
<hr/>

<div style="width:100%; text-align:center; font-weight:bold">***Spielerstatus***</div>
<table style="width:100%">
	<tr>
		<td>
			<img src="../media/images/icons/user.png" style="vertical-align: middle" />
		</td>
		<td>
			Spielername
		</td>
		<td>
			<?php echo $currentPlayer->name; ?>
		</td>
	</tr>
	<tr>
		<td>
			<img src="../media/images/icons/points.png" style="vertical-align: middle" />
		</td>
		<td>
			Punktzahl
		</td>
		<td>
			<?php echo $currentPlayer->score; ?>
		</td>
	</tr>
</table>
<hr/>
<div style="width:100%; text-align:center; font-weight:bold">***Nachrichtenstatus***</div>
<?php
	if ($gameSettings->protocol == "aodv") {
		$messageTable = "AODVDataPacket";
	}
?>
<?php $playerMessage = Doctrine_Query::create()->from($messageTable)->where("ownerId = ?", $currentPlayer->id)->execute()->getFirst() ?>
<?php if ((empty($playerMessage))): ?>
	<table style="width:100%">
		<th colspan="3" style="text-align:center; width:300px">
			Start- und Zielknoten wählen
		</th>
		<tr>
			<td>
				<img src="../media/images/icons/flag-white.png" style="vertical-align: middle" />
			</td>
			<td>
				Startknoten-ID:
			</td>
			<td>
				<span id="sourceNode"></span>
			</td>
		</tr>
		<tr>
			<td>
				<img src="../media/images/icons/flag-black.png" style="vertical-align: middle" />
			</td>
			<td>
				Zielknoten-ID
			</td>
			<td>
				<span id="destinationNode"></span>
			</td>
		</tr>
		<tr>
			<td>
				<img src="../media/images/icons/star.png" style="vertical-align: middle" />
			</td>
			<td>
				Bonusziel-ID
			</td>
			<td>
				<?php echo $gameSettings->bonusGoal ?>
			</td>
		</tr>
	</table>
	<div style="width:100%; text-align:center">
		<button id="sendMessageButton" type="button">absenden</button>
		<button type="button" onclick="resetPlayerMessage()">
			zurücksetzen
		</button>
	</div>
<?php else: ?>
	<table style="width:100%">
		<tr>
			<td>
				<img src="../media/images/icons/network-wireless-small.png" style="vertical-align: middle" />
			</td>
			<td>
				Akt. Knoten-ID
			</td>
			<td>
				<?php echo $playerMessage->CurrentNode->id ?>
			</td>
		</tr>
		<tr>
			<td>
				<img src="../media/images/icons/network-wireless-small.png" style="vertical-align: middle" />
			</td>
			<td>
				Zielknoten-ID
			</td>
			<td>
				<?php echo $playerMessage->Destination->id ?>
			</td>
		</tr>
		<tr>
			<td>
				<img src="../media/images/icons/box-search-result.png" style="vertical-align: middle" />
			</td>
			<td>
				Status
			</td>
			<td>
				<?php echo $playerMessage->getStatus(true) ?>
			</td>
		</tr>
		<?php if ($playerMessage->status == AODV_DATA_PACKET_STATUS_WAITING_FOR_ROUTE || $playerMessage->status == AODV_DATA_PACKET_STATUS_ERROR): ?>
			<tr>
				<td>

				</td>
				<td>
					<?php if ($gameSettings->protocol == "aodv"): ?>
						Anzahl Routenanfragen (RREQs)
					<?php endif ?>
				</td>
				<td>
					<?php if ($gameSettings->protocol == "aodv"): ?>
					 	<?php echo $RREQCount = Doctrine_Query::create()->from("AODVRoutingMessage")->where("type = ? AND sourceId = ? AND destinationId = ?", array(AODV_ROUTING_MESSAGE_TYPE_RREQ, $playerMessage->sourceId, $playerMessage->destinationId))->execute()->count(); ?>
					<?php endif ?>
				</td>
			</tr>
		<?php else: ?>
			<tr>
				<td>

				</td>
				<td>
					Sprünge
				</td>
				<td>
					<?php echo $playerMessage->hopsDone ?>
				</td>
			</tr>
		<?php endif ?>
	</table>
	<?php if ($playerMessage->status == AODV_DATA_PACKET_STATUS_ARRIVED): ?>
		<div style="width:100%; text-align:center">
			<button type="button" onclick="resetPlayerMessage(<?php echo $currentPlayer->id ?>)">
				Neue Nachricht
			</button>
		</div>		
	<?php endif ?>
	<?php if ($playerMessage->status == AODV_DATA_PACKET_STATUS_ERROR): ?>
		<div style="width:100%; text-align:center">
			<button id="resendButton" type="button" onclick="resendRoutingMessages(<?php echo $playerMessage->sourceId ?>, <?php echo $playerMessage->destinationId ?>, <?php echo $currentPlayer->id ?>)">
				Routenanfrage neu versenden
			</button>
			<button type="button" onclick="resetPlayerMessage()">
				zurücksetzen
			</button>
		</div>
	<?php endif ?>
	<?php if ($playerMessage->status == AODV_DATA_PACKET_STATUS_NODE_BUSY): ?>
		<div style="width:100%; text-align:center">
			<button type="button" onclick="resetPlayerMessage()">
				zurücksetzen
			</button>
		</div>
	<?php endif ?>
<?php endif ?>
<hr/>
<?php 
	if ($playerMessage->status == AODV_DATA_PACKET_STATUS_ARRIVED) {
		$hintMessage = "Deine Nachricht hat ihr Ziel erreicht. Gratuliere! Du hast ".$playerMessage->calculatePoints()." <img src='../media/images/icons/points.png' /> erhalten.";
		$hintStyle = "ui-state-highlight";
    } else if ($playerMessage->status == AODV_DATA_PACKET_STATUS_ERROR && $RREQCount == 0) {	
		$hintMessage = "Es konnte keine Route vom Start zum Ziel gefunden werden. Versende die Routenanfrage erneut oder wähle zwei andere Knoten.";
		$hintStyle = "ui-state-error";
	} else {
		$generalResponses = array(
			"Wenn du eine Nachricht erfolgreich zum Bonuszielknoten (der Knoten mit dem kleinen Stern) sendest, erhältst du 150% der üblichen Punkte.",
			"Der Bonsuzielknoten wird neue gesetzt sobald ein Spieler eine Nachricht erfolgreich zu ihm gesendet hat oder der Knoten aus dem Spiel ausscheidet.",
			"Nachrichten über kurze Strecken sind weniger von Störungen betroffen, bringen aber auch weniger Punkte."
		);
	
		$hintMessage = $generalResponses[mt_rand(0, count($generalResponses) - 1)];
		$hintStyle = "ui-state-highlight";
	}
?>
<div class="ui-corner-all <?php echo $hintStyle ?>" style="text-align:center">
	<span class='ui-icon ui-icon-info' style='float: left; margin-right: .3em;'></span>
	<?php echo $hintMessage ?>
</div>