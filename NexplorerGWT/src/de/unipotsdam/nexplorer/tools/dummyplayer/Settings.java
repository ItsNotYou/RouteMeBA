package de.unipotsdam.nexplorer.tools.dummyplayer;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class Settings {

	public PlayerInfo node;
	public GameInfo stats;
}
