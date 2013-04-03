package de.unipotsdam.nexplorer.client.admin.viewcontroller;

import de.unipotsdam.nexplorer.shared.GameStats;

public class JSNI {
	
	/**
	 * stellt sicher, dass auch bei einem reload das HTML des Spiel pausieren
	 * button den Zustand des System widerspiegelt
	 * 
	 * @param gamePaused
	 */
	public static native void setEnded()/*-{
		$wnd.setEnded();
	}-*/;

	public static native void setNotEnded()/*-{
		$wnd.setNotEnded();
	}-*/;

	private static native void setPaused() /*-{
		$wnd.setPaused();
	}-*/;

	private static native void setNotPaused()/*-{
		$wnd.setNotPaused();
	}-*/;

	public static void updateGameState(GameStats result) {
		switch (result.getGameStatus()) {
		case NOTSTARTED:
			JSNI.setNotEnded();
			break;
		case HASENDED:
			JSNI.setEnded();
			break;
		case ISPAUSED:
			JSNI.setPaused();
			break;
		case ISRUNNING:
			JSNI.setNotPaused();
			break;
		}
	}

}