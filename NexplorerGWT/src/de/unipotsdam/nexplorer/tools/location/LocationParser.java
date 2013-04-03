package de.unipotsdam.nexplorer.tools.location;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import javax.swing.JFileChooser;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import de.unipotsdam.nexplorer.shared.SaveLocationRequest;
import de.unipotsdam.nexplorer.shared.SessionLocation;

public class LocationParser {

	public static void main(String[] args) throws IOException, ParseException {
		new LocationParser();
	}

	private LocationParser() throws IOException, ParseException {
		Collection<File> files = getFiles();
		for (File file : files) {
			String fileContent = readFile(file);
			if (fileContent == null) {
				return;
			}

			Collection<SessionLocation> locations = new LinkedList<SessionLocation>();
			new LocationSaver().read(fileContent, UUID.randomUUID(), locations);
			sendToServer(locations);
		}
	}

	private void sendToServer(Collection<SessionLocation> events) throws IOException {
		Client client = Client.create();
		try {
			WebResource resource = client.resource("http://127.0.0.1:8080/rest/accuracy/saveWithSession");

			SaveLocationRequest request = new SaveLocationRequest();
			request.setLocations(events);
			resource.type(MediaType.APPLICATION_JSON).post(request);
		} finally {
			client.destroy();
		}
	}

	private Collection<File> getFiles() {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);

		int result = chooser.showOpenDialog(null);
		if (result != JFileChooser.APPROVE_OPTION) {
			return null;
		}

		File[] files = chooser.getSelectedFiles();
		return Arrays.asList(files);
	}

	private String readFile(File log) throws IOException {
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
}
