package de.unipotsdam.nexplorer.tools.dummyplayer;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import de.unipotsdam.nexplorer.shared.GameStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameSettings {

	public double playingFieldLowerRightLatitude;
	public double playingFieldLowerRightLongitude;
	public double playingFieldUpperLeftLatitude;
	public double playingFieldUpperLeftLongitude;
	public GameStatus gameState;
}
