package de.unipotsdam.nexplorer.server.persistence;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.shared.Locatable;
import de.unipotsdam.nexplorer.shared.Location;

public class Item implements Locatable {

	private final Items inner;
	private DatabaseImpl dbAccess;

	@Inject
	public Item(@Assisted Items inner, DatabaseImpl dbAccess) {
		this.inner = inner;
		this.dbAccess = dbAccess;
	}

	public Items inner() {
		return inner;
	}

	public void delete() {
		dbAccess.delete(inner);
	}

	@Override
	public double getLongitude() {
		return inner().getLongitude();
	}

	@Override
	public double getLatitude() {
		return inner().getLatitude();
	}

	public Long getId() {
		return inner().getId();
	}

	public Location getLocation() {
		return new Location(getLatitude(), getLongitude());
	}
}
