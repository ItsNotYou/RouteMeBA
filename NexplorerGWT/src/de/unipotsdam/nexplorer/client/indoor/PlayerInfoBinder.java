package de.unipotsdam.nexplorer.client.indoor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import de.unipotsdam.nexplorer.client.IndoorServiceImpl;
import de.unipotsdam.nexplorer.client.indoor.dto.UiInfo;
import de.unipotsdam.nexplorer.client.indoor.levels.AvailableNodeUpdater;
import de.unipotsdam.nexplorer.client.indoor.levels.RouteKeeper;
import de.unipotsdam.nexplorer.client.indoor.view.messaging.ActiveRouting;
import de.unipotsdam.nexplorer.client.indoor.view.messaging.LevelOneRouteSelection;
import de.unipotsdam.nexplorer.client.indoor.view.messaging.LevelTwoRouteSelection;
import de.unipotsdam.nexplorer.client.indoor.view.messaging.RoutingLevel;
import de.unipotsdam.nexplorer.client.indoor.viewcontroller.ButtonSetShown;
import de.unipotsdam.nexplorer.client.indoor.viewcontroller.IndoorStatsTimer;
import de.unipotsdam.nexplorer.client.util.HasTable;
import de.unipotsdam.nexplorer.shared.Aodv;
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

	private IndoorStatsTimer indoorStatsUpdater;
	private final ActiveRouting activeRouting;
	private RoutingLevel level;

	/**
	 * depending on the state either the message table is shown or the messageStatusTable depending on the Status of the message gameOptions are blended in
	 * 
	 * @param level
	 * 
	 */
	public PlayerInfoBinder() {
		setElement(uiBinder.createAndBindUi(this));
		this.activeRouting = new ActiveRouting();
		this.level = null;

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

	/**
	 * kümmerst sich darum, dass die aktuellen Informationen des Spielers angezeigt werden
	 * 
	 * @param info
	 */
	public void updatePlayerInfos(UiInfo info) {
		// Set level sensitive routing panel if not already set
		if (level == null) {
			if (info.getPlayer().getDifficulty() == 1) {
				level = new LevelOneRouteSelection();
			} else if (info.getPlayer().getDifficulty() == 2) {
				LevelTwoRouteSelection level = new LevelTwoRouteSelection();
				level.addClickHandler(new LevelTwoHandler());

				RouteKeeper keeper = new RouteKeeper();
				keeper.setRouteCount(10);
				keeper.addRouteListener(level);
				AvailableNodeUpdater.addListener(keeper);
				level.addClickHandler(new RouteRemover(keeper));

				this.level = level;
			}
			this.currentRouteView.appendChild(level.getElement());
		}

		if (info.getPlayer() != null) {
			this.currentPlayerName.setInnerText(info.getPlayer().getName());
			this.currentPlayerScore.setInnerText(info.getPlayer().getScore());
		}
		if (info.getDataPacketSend() != null) {
			this.activeRouting.setSourceNode(info.getDataPacketSend().getSourceNodeId());
			this.activeRouting.setDestinationNode(info.getDataPacketSend().getDestinationNodeId());
			this.hintMessage.setInnerHTML(statusToHTMLString(info));
			this.activeRouting.setCurrentNodeId(info.getDataPacketSend().getCurrentNodeId());
		} else {
			this.hintMessage.setInnerHTML(getHintMessage(info));
		}
		this.remainingPlayingTime.setInnerText(TimeManager.convertToReadableTimeSpan(info.getRemainingTime()));
		this.activeRouting.setBonusGoal(info.getBonusGoal());

	}

	/**
	 * Gibt den aktuellen Zustand aus
	 * 
	 * @param info
	 * @return
	 */
	private String statusToHTMLString(UiInfo info) {
		Byte status = info.getDataPacketSend().getStatus();
		switch (status) {
		case Aodv.DATA_PACKET_STATUS_ARRIVED:
			return "Deine Nachricht hat ihr Ziel erreicht. Gratuliere! Du hast " + info.getDataPacketSend().getAwardedScore() + "<img src=\"/media/images/icons/points.png\"/> erhalten!";
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
	private String getHintMessage(UiInfo info) {
		if (info.getHint() != null) {
			return info.getHint();
		} else {
			return "Wenn du eine Nachricht erfolgreich zum Bonuszielknoten (der Knoten mit dem kleinen Stern) sendest, erhältst du 150% der üblichen Punkte. " + "Der Bonuszielknoten wird neu gesetzt sobald ein Spieler eine Nachricht erfolgreich zu ihm gesendet hat oder der Knoten aus dem Spiel ausscheidet. " + "Nachrichten über kurze Strecken sind weniger von Störungen betroffen, bringen aber auch weniger Punkte. ";
		}
	}

	public void switchToButtonState(ButtonSetShown state) {
		DivElement divElement = this.currentRouteView;
		if (state == ButtonSetShown.Other) {
			removeChildren(divElement);
			divElement.appendChild(activeRouting.getElement());
		} else {
			removeChildren(divElement);
			divElement.appendChild(level.getElement());
		}
	}

	private void removeChildren(DivElement element) {
		while (element.hasChildNodes()) {
			element.getChild(0).removeFromParent();
		}
	}
}
