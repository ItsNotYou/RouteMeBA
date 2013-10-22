package de.unipotsdam.nexplorer.client.admin.viewcontroller;

import de.unipotsdam.nexplorer.shared.GameStats;

public class JSNI {
	
	/**
	 * Diese Methode stellt sicher, dass auch bei einem Reload der Website
	 * das HTML des Spiel-Pausieren-Button		
	 * den Zustand des System widerspiegelt
	 * @param gamePaused
	 */
	public static native void setEnded()/*-{
		$wnd.setEnded();
	}-*/;

	public static native void setNotEnded()/*-{
		$wnd.setNotEnded();
	}-*/;
	
	public static void updateGameState(GameStats result) {
		switch (result.getGameStatus()) {
		case NOTSTARTED:
			JSNI.setNotEnded();
			break;
		case HASENDED:
			JSNI.setEnded();
			break;	
		}
	}

}