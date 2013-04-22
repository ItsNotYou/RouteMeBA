package de.unipotsdam.nexplorer.tools.dummyplayer;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerInfo {

	public int nearbyItemsCount;
	public Long nextItemDistance;
	public Long itemInCollectionRange;
}
