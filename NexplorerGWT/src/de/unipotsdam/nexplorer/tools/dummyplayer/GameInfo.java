package de.unipotsdam.nexplorer.tools.dummyplayer;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameInfo {

	public GameSettings settings;
}
