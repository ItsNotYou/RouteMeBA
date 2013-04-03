package de.unipotsdam.nexplorer.shared;

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SaveLocationRequest {

	private Collection<SessionLocation> locations;

	public Collection<SessionLocation> getLocations() {
		return locations;
	}

	public void setLocations(Collection<SessionLocation> locations) {
		this.locations = locations;
	}
}
