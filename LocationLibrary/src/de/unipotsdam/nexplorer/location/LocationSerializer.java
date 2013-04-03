package de.unipotsdam.nexplorer.location;

import java.util.Collection;

import de.unipotsdam.nexplorer.location.shared.SavedLocation;
import de.unipotsdam.nexplorer.location.shared.SavedLocations;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * 
 * @author hgessner
 * @deprecated Use {@link SavedLocations} and Android JSON support / Jersey support instead instead
 */
public class LocationSerializer {

	public String toJSON(Collection<SavedLocation> locations) {
		return new JSONSerializer().serialize(locations);
	}

	public Collection<SavedLocation> fromJSON(String json) {
		return new JSONDeserializer<Collection<SavedLocation>>().deserialize(json);
	}
}
