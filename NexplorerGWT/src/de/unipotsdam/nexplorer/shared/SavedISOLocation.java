package de.unipotsdam.nexplorer.shared;

import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.DateTime;

import de.unipotsdam.nexplorer.location.shared.SavedLocation;
import de.unipotsdam.nexplorer.server.rest.adapter.ISODateAdapter;

@XmlRootElement
public class SavedISOLocation extends SavedLocation {

	private DateTime timestamp;
	private UUID key;

	@Override
	public void setTimestamp(long timestamp) {
		super.setTimestamp(timestamp);

		Date tmp = new Date();
		tmp.setTime(timestamp);
		this.timestamp = new DateTime(tmp);
	}

	@XmlJavaTypeAdapter(ISODateAdapter.class)
	public DateTime getISOTimestamp() {
		return this.timestamp;
	}

	public void setSessionKey(UUID key) {
		this.key = key;
	}

	public UUID getSessionKey() {
		return this.key;
	}
}
