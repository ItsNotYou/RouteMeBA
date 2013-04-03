package de.unipotsdam.nexplorer.server.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class ISODateAdapter extends XmlAdapter<String, DateTime> {

	private DateTimeFormatter formatter;

	public ISODateAdapter() {
		this.formatter = ISODateTimeFormat.dateTime();
	}

	@Override
	public DateTime unmarshal(String v) throws Exception {
		return formatter.parseDateTime(v);
	}

	@Override
	public String marshal(DateTime v) throws Exception {
		return v.toString(formatter);
	}
}
