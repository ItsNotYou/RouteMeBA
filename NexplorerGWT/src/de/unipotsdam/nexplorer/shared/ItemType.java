package de.unipotsdam.nexplorer.shared;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public enum ItemType implements Serializable {
	@JsonProperty("BOOSTER")
	BOOSTER,
	@JsonProperty("BATTERY")
	BATTERY
}
