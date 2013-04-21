package de.unipotsdam.nexplorer.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

import de.unipotsdam.nexplorer.client.indoor.IndoorWrapper;
import de.unipotsdam.nexplorer.client.indoor.levels.AvailableNodeUpdater;

public class IndoorGWT implements EntryPoint {

	@Override
	public void onModuleLoad() {
		AvailableNodeUpdater.exportStaticMethod();

		IndoorWrapper indoorWrapper = new IndoorWrapper();
		RootPanel.get("contents").add(indoorWrapper);
		loginIndoor();
	}

	public static native void loginIndoor() /*-{
		$wnd.loginIndoor();
	}-*/;

	public static native void setGameStatus(int isRunning, int gameDidEnd, int gameExists, int gameDidExist)/*-{
		$wnd.setGameStatus(isRunning, gameDidEnd, gameExists, gameDidExist);
	}-*/;

}
