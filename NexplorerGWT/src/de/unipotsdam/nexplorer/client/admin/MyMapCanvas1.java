package de.unipotsdam.nexplorer.client.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import de.unipotsdam.nexplorer.client.util.DivElementWrapper;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.PlayingField;

/**
 * wraps the canvas
 * @author Julian
 *
 */
public class MyMapCanvas1 extends DivElementWrapper {

	/**
	 * black magic
	 */
	private static MyMapCanvas1UiBinder uiBinder = GWT
			.create(MyMapCanvas1UiBinder.class);

	interface MyMapCanvas1UiBinder extends UiBinder<Widget, MyMapCanvas1> {
	}
	

	public MyMapCanvas1() {
		initWidget(uiBinder.createAndBindUi(this));				
	}
	
	public void showField(PlayingField playingField) {
		Double latitude = playingField.getCenterOfLatitude();
		Double longitude = playingField.getCenterOfLongitude();
		startGame(latitude,longitude, playingField.getUpperLeft().getLatitude(), playingField.getUpperLeft().getLongitude(), playingField.getLowerRight().getLatitude(), playingField.getLowerRight().getLongitude());
	}
	
	/**
	 * from here the canvas is taken over by native JS code
	 * @param latitude
	 * @param longitude
	 * @param playingFieldUpperLeftLatitude
	 * @param playingFieldUpperLeftLongitude
	 * @param playingFieldLowerRightLatitude
	 * @param playingFieldLowerRightLongitude
	 */
	public static native void startGame(double latitude, double longitude, double playingFieldUpperLeftLatitude,double playingFieldUpperLeftLongitude, double playingFieldLowerRightLatitude, double  playingFieldLowerRightLongitude ) /*-{
	  $wnd.initializeAdminMap(latitude,longitude,playingFieldUpperLeftLatitude,playingFieldUpperLeftLongitude,playingFieldLowerRightLatitude,playingFieldLowerRightLongitude);
	}-*/;
}
