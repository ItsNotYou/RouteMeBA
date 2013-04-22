package de.unipotsdam.nexplorer.tools.dummyplayer;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResult {

	public int id;
	public String errorMessage;
}
