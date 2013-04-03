package de.unipotsdam.nexplorer.tools.performance;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Pattern;

import org.hibernate.Session;
import org.hibernate.Transaction;

import de.unipotsdam.nexplorer.server.persistence.hibernate.HibernateSessions;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Performance;

public class PerformanceParser {

	public static void main(String[] args) throws IOException, ParseException {
		new PerformanceParser();
	}

	private PerformanceParser() throws IOException, ParseException {
		String fileContent = readFile();
		String[] rawEvents = splitIntoEvents(fileContent);
		Collection<Event> events = parseEvents(rawEvents);
		writeToDatabase(events);
	}

	private void writeToDatabase(Collection<Event> events) {
		Session session = HibernateSessions.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();

		long count = 0;
		for (Event event : events) {
			Performance info = new Performance();
			info.setWhat(event.What);
			info.setWhen(event.When);
			info.setHowLong(event.HowLong);
			session.save(info);

			count++;
			if (count % 100 == 0) {
				session.flush();
				session.clear();
			}
		}

		session.flush();
		tx.commit();
		session.close();
	}

	private Collection<Event> parseEvents(String[] rawEvents) throws ParseException {
		Collection<Event> result = new LinkedList<Event>();
		for (String rawEvent : rawEvents) {
			result.add(parseFrom(rawEvent));
		}
		return result;
	}

	private String[] splitIntoEvents(String fileContent) {
		return fileContent.split(Pattern.quote("\r\n"));
	}

	private String readFile() throws IOException {
		File log = new File("war/logs/performance.log");
		FileReader reader = new FileReader(log);
		try {
			StringBuilder builder = new StringBuilder();
			char[] buffer = new char[1024];
			int read = 0;
			while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
				builder.append(buffer, 0, read);
			}
			return builder.toString();
		} finally {
			reader.close();
		}
	}

	/**
	 * Message example: "20.11.2012-13:43:11.028 updateNeighbours took 165ms"
	 * 
	 * @param message
	 * @return
	 * @throws ParseException
	 */
	private Event parseFrom(String message) throws ParseException {
		String[] parts = message.split(Pattern.quote(" "));

		Event result = new Event();
		result.When = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss.SSS").parse(parts[0]);
		result.What = parts[1];
		result.HowLong = parseDuration(parts[3]);
		return result;
	}

	private long parseDuration(String duration) {
		String number = duration.substring(0, duration.indexOf("ms"));
		return Long.parseLong(number);
	}

	private class Event {

		public Date When;
		public String What;
		public long HowLong;
	}
}
