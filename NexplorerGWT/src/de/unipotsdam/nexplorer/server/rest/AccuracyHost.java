package de.unipotsdam.nexplorer.server.rest;

import static org.hibernate.criterion.Restrictions.eq;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.hibernate.Session;

import de.unipotsdam.nexplorer.location.LocationSerializer;
import de.unipotsdam.nexplorer.location.shared.SavedLocation;
import de.unipotsdam.nexplorer.location.shared.SavedLocations;
import de.unipotsdam.nexplorer.server.data.Unit;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.LocationLog;
import de.unipotsdam.nexplorer.shared.SaveLocationRequest;
import de.unipotsdam.nexplorer.shared.SavedISOLocation;
import de.unipotsdam.nexplorer.shared.SessionLocation;
import de.unipotsdam.nexplorer.tools.location.LocationSaver;

/**
 * Rest interface for accuracy logging. Isn't used in the game, but in the preparations to find a good playfield based on GPS accuracy.
 * 
 * @author Hendrik Geßner <hgessner@uni-potsdam.de>
 * 
 */
@Path("/accuracy")
public class AccuracyHost {

	@GET
	@Path("read")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<SavedISOLocation> read(@QueryParam("provider") String provider) {
		Unit unit = new Unit();
		try {
			Collection<SavedISOLocation> result = new LinkedList<SavedISOLocation>();

			Session session = unit.resolve(Session.class);
			List<LocationLog> query = session.createCriteria(LocationLog.class).add(eq("provider", provider)).list();
			for (LocationLog log : query) {
				SavedISOLocation location = new SavedISOLocation();
				location.setAccuracy(log.getAccuracy());
				location.setLatitude(log.getLatitude());
				location.setLongitude(log.getLongitude());
				location.setProvider(log.getProvider());
				location.setTimestamp(log.getWhen().getTime());
				location.setSessionKey(log.getSessionKey());
				result.add(location);
			}

			return result;
		} finally {
			unit.close();
		}
	}

	/**
	 * 
	 * @param rawLocations
	 * @deprecated Use {@link #saveObject(SavedLocations)} instead
	 */
	@POST
	@Path("save")
	public void save(@FormParam("locations") String rawLocations) {
		Collection<SavedLocation> locations = new LocationSerializer().fromJSON(rawLocations);
		saveLocations(locations);
	}

	private void saveLocations(Collection<SavedLocation> locations) {
		UUID sessionKey = UUID.randomUUID();

		Collection<SessionLocation> result = new LinkedList<SessionLocation>();
		for (SavedLocation location : locations) {
			SessionLocation session = new SessionLocation();
			session.setAccuracy(location.getAccuracy());
			session.setLatitude(location.getLatitude());
			session.setLongitude(location.getLongitude());
			session.setProvider(location.getProvider());
			session.setTimestamp(location.getTimestamp());
			session.setSessionKey(sessionKey);

			result.add(session);
		}

		SaveLocationRequest request = new SaveLocationRequest();
		request.setLocations(result);
		saveWithSession(request);
	}

	@POST
	@Path("saveObject")
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveObject(SavedLocations locations) {
		saveLocations(locations.getLocations());
	}

	@POST
	@Path("saveWithSession")
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveWithSession(SaveLocationRequest locationsRequest) {
		writeToFile(locationsRequest.getLocations(), UUID.randomUUID());
		writeToDatabase(locationsRequest.getLocations());
		LogManager.getLogger(getClass()).info("Received {} locations", locationsRequest.getLocations().size());
	}

	private void writeToFile(Collection<SessionLocation> locations, UUID defaultSessionKey) {
		String directory = "logs";
		String filename = "accuracy.log";

		FileWriter writer = null;
		try {
			new File(directory).mkdirs();
			writer = new FileWriter(directory + "/" + filename, true);

			new LocationSaver().write(locations, writer);
		} catch (IOException e) {
			LogManager.getLogger(getClass()).error("Could not write locations log", e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					LogManager.getLogger(getClass()).error("Could not close locations log", e);
				}
			}
		}
	}

	private void writeToDatabase(Collection<SessionLocation> events) {
		Unit unit = new Unit();
		try {
			Session session = unit.resolve(Session.class);

			long count = 0;
			for (SessionLocation event : events) {
				LocationLog info = new LocationLog();
				info.setAccuracy(event.getAccuracy());
				info.setLatitude(event.getLatitude());
				info.setLongitude(event.getLongitude());
				info.setProvider(event.getProvider());
				info.setWhen(toDate(event.getTimestamp()));
				info.setSessionKey(event.getSessionKey());
				session.save(info);

				count++;
				if (count % 100 == 0) {
					session.flush();
					session.clear();
				}
			}
		} finally {
			unit.close();
		}
	}

	private Date toDate(long timestamp) {
		Date result = new Date();
		result.setTime(timestamp);
		return result;
	}
}
