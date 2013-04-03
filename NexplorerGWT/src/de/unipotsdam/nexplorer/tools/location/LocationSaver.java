package de.unipotsdam.nexplorer.tools.location;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import de.unipotsdam.nexplorer.shared.SessionLocation;

public class LocationSaver {

	private static final String NEWLINE = "\r\n";
	private static final String COLUMN_SEPARATOR = ";";

	private void parseFrom(String message, int lineNumber, UUID defaultSessionKey, Collection<SessionLocation> parsed) throws ParseException {
		String[] parts = message.split(Pattern.quote(COLUMN_SEPARATOR));

		if (isHeader(parts)) {
			return;
		}

		try {
			SessionLocation result = new SessionLocation();
			result.setLongitude(Double.parseDouble(parts[0]));
			result.setLatitude(Double.parseDouble(parts[1]));
			result.setAccuracy(Double.parseDouble(parts[2]));
			result.setProvider(parts[3]);
			result.setTimestamp(Long.parseLong(parts[4]));

			if (parts.length >= 6) {
				result.setSessionKey(UUID.fromString(parts[5]));
			} else {
				result.setSessionKey(defaultSessionKey);
			}

			parsed.add(result);
		} catch (NumberFormatException e) {
			System.err.println("Error in line " + lineNumber);
			e.printStackTrace();
		}
	}

	private void write(SessionLocation location, Appendable writer) throws IOException {
		writer.append(Double.toString(location.getLongitude()));
		writer.append(COLUMN_SEPARATOR);
		writer.append(Double.toString(location.getLatitude()));
		writer.append(COLUMN_SEPARATOR);
		writer.append(Double.toString(location.getAccuracy()));
		writer.append(COLUMN_SEPARATOR);
		writer.append(location.getProvider());
		writer.append(COLUMN_SEPARATOR);
		writer.append(Long.toString(location.getTimestamp()));
		writer.append(COLUMN_SEPARATOR);
		writer.append(location.getSessionKey().toString());
		writer.append(NEWLINE);
	}

	private boolean isHeader(String[] parts) {
		return parts[0].equals("Longitude");
	}

	private void writeHeader(Appendable writer) throws IOException {
		writer.append("Longitude");
		writer.append(COLUMN_SEPARATOR);
		writer.append("Latitude");
		writer.append(COLUMN_SEPARATOR);
		writer.append("Accuracy");
		writer.append(COLUMN_SEPARATOR);
		writer.append("Provider");
		writer.append(COLUMN_SEPARATOR);
		writer.append(Long.toString(new Date().getTime()));
		writer.append(COLUMN_SEPARATOR);
		writer.append("Session");
		writer.append(NEWLINE);
	}

	public void write(Collection<SessionLocation> locations, Appendable writer) throws IOException {
		writeHeader(writer);
		for (SessionLocation location : locations) {
			write(location, writer);
		}
	}

	public void read(String fileContent, UUID defaultSessionKey, Collection<SessionLocation> result) throws ParseException {
		List<String> lines = new LinkedList<String>();
		splitIntoEvents(fileContent, lines);
		parse(lines, defaultSessionKey, result);
	}

	private void splitIntoEvents(String fileContent, List<String> lines) {
		String[] result = fileContent.split(Pattern.quote(NEWLINE));
		lines.addAll(Arrays.asList(result));
	}

	private void parse(List<String> rawEvents, UUID defaultSessionKey, Collection<SessionLocation> result) throws ParseException {
		int lineNumber = 1;
		for (String rawEvent : rawEvents) {
			parseFrom(rawEvent, lineNumber, defaultSessionKey, result);
			lineNumber++;
		}
	}
}