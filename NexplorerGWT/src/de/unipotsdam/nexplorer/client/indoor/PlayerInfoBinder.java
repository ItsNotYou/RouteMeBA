package de.unipotsdam.nexplorer.client.indoor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import de.unipotsdam.nexplorer.client.IndoorServiceImpl;
import de.unipotsdam.nexplorer.client.indoor.view.messaging.ActiveRouting;
import de.unipotsdam.nexplorer.client.indoor.viewcontroller.IndoorStatsTimer;
import de.unipotsdam.nexplorer.client.util.HasTable;
import de.unipotsdam.nexplorer.shared.Aodv;
import de.unipotsdam.nexplorer.shared.PlayerInfo;
import de.unipotsdam.nexplorer.shared.TimeManager;

public class PlayerInfoBinder extends HasTable {

	private static PlayerInfoBinderUiBinder uiBinder = GWT.create(PlayerInfoBinderUiBinder.class);

	interface PlayerInfoBinderUiBinder extends UiBinder<Element, PlayerInfoBinder> {
	}

	@UiField
	DivElement remainingPlayingTime;
	@UiField
	DivElement currentPlayerName;
	@UiField
	DivElement currentPlayerScore;
	@UiField
	DivElement hintMessage;
	@UiField
	DivElement currentRouteView;
	/**
	 * contains either NewMessageBinder or NewRouteRequestBinder or ResetPLayerMessageBinder
	 */
	@UiField
	DivElement playOptions;

	private final SimpleIndoorBinder simpleIndoorBinder;
	private IndoorStatsTimer indoorStatsUpdater;
	private final ActiveRouting activeRouting;

	/**
	 * depending on the state either the message table is shown or the messageStatusTable depending on the Status of the message gameOptions are blended in
	 * 
	 */
	public PlayerInfoBinder(SimpleIndoorBinder simpleIndoorBinder) {
		setElement(uiBinder.createAndBindUi(this));
		this.activeRouting = new ActiveRouting();
		this.currentRouteView.appendChild(activeRouting.getElement());
		// store simpleIndoorBinder for hooks
		this.simpleIndoorBinder = simpleIndoorBinder;
		// create intervals
		getFrequency();
	}

	public void finishConstructorAfterUpdate(int frequency) {
		// indoor service
		this.indoorStatsUpdater = new IndoorStatsTimer(this);
		indoorStatsUpdater.scheduleRepeating(frequency);

	}

	private void getFrequency() {
		IndoorServiceImpl indoorServiceImpl = new IndoorServiceImpl();
		indoorServiceImpl.getUpdateDisplayFrequency(new FrequencyUpdater<Integer>(this));
	}

	public SimpleIndoorBinder getSimpleIndoorBinder() {
		return simpleIndoorBinder;
	}

	/**
	 * kümmerst sich darum, dass die aktuellen Informationen des Spielers angezeigt werden
	 * 
	 * @param result
	 */
	public void updatePlayerInfos(PlayerInfo result) {
		if (result.getPlayer() != null) {
			this.currentPlayerName.setInnerText(result.getPlayer().name);
			this.currentPlayerScore.setInnerText(result.getPlayer().score + "");
		}
		if (result.getDataPacketSend() != null) {
			this.activeRouting.getSourceNode().setInnerText(result.getDataPacketSend().getMessageDescription().getSourceNodeId() + "");
			this.activeRouting.getDestinationNode().setInnerText(result.getDataPacketSend().getMessageDescription().getDestinationNodeId() + "");

			// this.hintMessage.setInnerHTML(getHintMessage(result));
			this.hintMessage.setInnerHTML(statusToHTMLString(result));
			this.activeRouting.getCurrentNodeId().setInnerHTML(result.getDataPacketSend().getPlayersByCurrentNodeId().getId() + "");
		} else {
			this.hintMessage.setInnerHTML(getHintMessage(result));
		}
		this.remainingPlayingTime.setInnerText(TimeManager.convertToReadableTimeSpan(result.getRemainingTime()));
		this.activeRouting.getBonusGoal().setInnerText(result.getBonusGoal());

	}

	/**
	 * Gibt den aktuellen Zustand aus
	 * 
	 * @param result
	 * @return
	 */
	private String statusToHTMLString(PlayerInfo result) {
		Byte status = result.getDataPacketSend().getStatus();
		switch (status) {
		case Aodv.DATA_PACKET_STATUS_ARRIVED:
			return "Deine Nachricht hat ihr Ziel erreicht. Gratuliere! Du hast " + result.getDataPacketSend().getAwardedScore() + "<img src=\"/media/images/icons/points.png\"/> erhalten!";
		case Aodv.DATA_PACKET_STATUS_ERROR:
			return "Es konnte keine Route vom Start zum Ziel gefunden werden. Versende die Routenanfrage erneut oder wähle zwei andere Knoten!";
		case Aodv.DATA_PACKET_STATUS_NODE_BUSY:
			return "Ein Knoten ist überlastet. Warte oder schicke eine neue Nachricht!";
		case Aodv.DATA_PACKET_STATUS_UNDERWAY:
			return "Deine Nachricht ist unterwegs!";
		case Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE:
			return "Das Packet wartet auf eine Route!";
		case Aodv.DATA_PACKET_STATUS_CANCELLED:
			return "Datentransfer wurde abgebrochen!";
		default:
			return "";
		}
	}

	/**
	 * Gibt Hilfenachrichten aus
	 * 
	 * @return
	 */
	private String getHintMessage(PlayerInfo result) {
		if (result.getHint() != null) {
			return result.getHint();
		}
		return "Wenn du eine Nachricht erfolgreich zum Bonuszielknoten (der Knoten mit dem kleinen Stern) sendest, erhältst du 150% der üblichen Punkte. " + "Der Bonuszielknoten wird neu gesetzt sobald ein Spieler eine Nachricht erfolgreich zu ihm gesendet hat oder der Knoten aus dem Spiel ausscheidet. " + "Nachrichten über kurze Strecken sind weniger von Störungen betroffen, bringen aber auch weniger Punkte. ";
	}

	public DivElement getStatus() {
		return this.activeRouting.getStatus();
	}

	public DivElement getStatusMessage() {
		return this.activeRouting.getStatusMessage();
	}
}
