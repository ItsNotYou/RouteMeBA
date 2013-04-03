package de.unipotsdam.nexplorer.client.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.UIObject;

import de.unipotsdam.nexplorer.client.AdminServiceImpl;
import de.unipotsdam.nexplorer.client.admin.viewcontroller.DefaultValueUpdater;
import de.unipotsdam.nexplorer.client.admin.viewcontroller.GameStartedHandler;
import de.unipotsdam.nexplorer.client.admin.viewcontroller.InitialStatsUpdater;
import de.unipotsdam.nexplorer.client.admin.viewcontroller.StatsUpdateTimer;
import de.unipotsdam.nexplorer.client.util.DivElementWrapper;
import de.unipotsdam.nexplorer.client.util.HasWrapper;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.GameStatus;

/**
 * Dies ist die Hauptklasse von der AdminGUI 
 * Hier wird das Eingabefeld am Anfang bearbeitet,
 * wie auch die Adminsicht waehrend dem Spiel
 * 
 * @author Julian
 * 
 */
public class AdminBinder extends UIObject  {

	@UiField
	DivElement gameStats;
	@UiField
	public
	DivElement startGameForm;
	@UiField
	DivElement itemStats;
	@UiField
	DivElement playerStats;
	@UiField
	DivElement map_canvas;
	@UiField
	InputElement timeToPlay;
	@UiField
	InputElement baseNodeRange;
	@UiField
	InputElement rangeForCollectingStuff;
	@UiField
	InputElement numberOfBatteries;
	@UiField
	InputElement numberOfBoosters;
	@UiField
	SelectElement difficulty;
	@UiField
	SelectElement protocol;
	@UiField
	InputElement playingFieldUpperLeftLatitude;
	@UiField
	InputElement playingFieldUpperLeftLongitude;
	@UiField
	InputElement playingFieldLowerRightLongitude;
	@UiField
	InputElement playingFieldLowerRightLatitude;
	@UiField
	InputElement updatePositionIntervalTime;
	@UiField
	InputElement updateDisplayIntervalTime;
	@UiField
	ButtonElement submitButton;

	/**
	 * the admin gwt interface class
	 */
	private AdminServiceImpl adminService;
	/**
	 * holds the canvas where the players and their location are displayed
	 */
	private MyMapCanvas1 myMapCanvas;
	/**
	 * Show the stats of the game
	 */
	private GameStatsBinder gameStatsBinder;
	/**
	 * Show the player stats
	 */
	private PlayerStatsBinder playerStatsBinder;
	/**
	 * Show the status of the messages (items)
	 */
	private ItemStatsBinder itemStatsBinder;
	
	/**
	 * black magic
	 */
	private static AdminBinderUiBinder uiBinder = GWT.create(AdminBinderUiBinder.class);

	interface AdminBinderUiBinder extends UiBinder<Element, AdminBinder> {
	}

	/**
	 * Konstruktor
	 * 
	 * @param firstName
	 */
	public AdminBinder() {
		setElement(uiBinder.createAndBindUi(this));		

		// initialize Service
		this.adminService = new AdminServiceImpl(this);

		// wrap canvas
		this.setMyMapCanvas(new MyMapCanvas1());

		// wrap gamestats
		this.gameStatsBinder = new GameStatsBinder(this);
		// wrap playerstats
		this.playerStatsBinder = new PlayerStatsBinder();
		// wrap itemstats
		this.itemStatsBinder = new ItemStatsBinder();					

		// wrap submitButton
		wrapSubmitButton();
		
		// set default values if the game has not started and the admin is trying to pick the details
		adminService.getDefaultGameStats(new DefaultValueUpdater<Settings>(this));

		// see if game is started or start
		this.adminService.getGameStats(new InitialStatsUpdater<GameStats>(this));
	}
	
	public void startUpdateIntevals(Long frequency) {	
		StatsUpdateTimer statsUpdateTimer = new StatsUpdateTimer(this.getAdminService(), this);
		statsUpdateTimer.scheduleRepeating(frequency.intValue());
	}

	/**
	 * diese Methode macht aus dem HTML Button einen GWT Button
	 * der ClickHandler haben kann, eigentlich gibt es
	 * von GWT eine statische Methode Button.wrap(), aber das funktioniert 
	 * aus einem mir unbekannten Grund nicht
	 */
	private void wrapSubmitButton() {
		Button button = new Button(submitButton.getInnerHTML());
		DivElement divElement = (DivElement) submitButton.getParentElement();
		submitButton.removeFromParent();
		DOM.sinkEvents(button.getElement(), Event.ONCLICK);
		DOM.setEventListener(button.getElement(), new GameStartedHandler(this));
		divElement.appendChild(button.getElement());
	}

	public ButtonElement getSubmitButton() {
		return submitButton;
	}

	public AdminServiceImpl getAdminService() {
		return adminService;
	}

	public SelectElement getDifficulty() {
		return difficulty;
	}

	public InputElement getNumberOfBatteries() {
		return numberOfBatteries;
	}

	public DivElement getMap_canvas() {
		return map_canvas;
	}

	public InputElement getPlayingFieldLowerRightLongitude() {
		return playingFieldLowerRightLongitude;
	}

	public InputElement getPlayingFieldUpperLeftLatitude() {
		return playingFieldUpperLeftLatitude;
	}

	public InputElement getPlayingFieldUpperLeftLongitude() {
		return playingFieldUpperLeftLongitude;
	}

	public InputElement getPlayingFieldLowerRightLatitude() {
		return playingFieldLowerRightLatitude;
	}

	public InputElement getRangeForCollectingStuff() {
		return rangeForCollectingStuff;
	}

	public InputElement getNumberOfBoosters() {
		return numberOfBoosters;
	}

	public InputElement getBaseNodeRange() {
		return baseNodeRange;
	}

	public SelectElement getProtocol() {
		return protocol;
	}

	public InputElement getTimeToPlay() {
		return timeToPlay;
	}

	public InputElement getUpdateDisplayIntervalTime() {
		return updateDisplayIntervalTime;
	}

	public InputElement getUpdatePositionIntervalTime() {
		return updatePositionIntervalTime;
	}

	public GameStatsBinder getGameStatsBinder() {
		return gameStatsBinder;
	}

	public PlayerStatsBinder getPlayerStatsBinder() {
		return playerStatsBinder;
	}

	public ItemStatsBinder getItemStatsBinder() {
		return itemStatsBinder;
	}

	
	
	/**
	 * gamestats hinzufügen
	 */
	public void showGameStats() {
		gameStats.appendChild(gameStatsBinder.getElement());
	}

	/**
	 * playerstats hinzufügen
	 */
	public void showPlayerStats() {
		playerStats.appendChild(playerStatsBinder.getElement());
	}

	/**
	 * itemstats hinzufügen
	 */
	public void showItemStats() {
		itemStats.appendChild(itemStatsBinder.getElement());
	}
	

	/**
	 * @param myMapCanvas the myMapCanvas to set
	 */
	public void setMyMapCanvas(MyMapCanvas1 myMapCanvas) {
		this.myMapCanvas = myMapCanvas;
	}

	/**
	 * @return the myMapCanvas
	 */
	public MyMapCanvas1 getMyMapCanvas() {
		return myMapCanvas;
	}
	
	

}
