package de.unipotsdam.nexplorer.shared;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.gwt.user.client.rpc.IsSerializable;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

/**
 * 
 * @author Julian
 *
 */
public class PlayerStats implements IsSerializable {

	@JsonProperty("messagers")
	private List<Messager> messagers;
	@JsonProperty("nodes")
	private List<Players> nodes;

	public PlayerStats() {
		// TODO Auto-generated constructor stub
	}
	
	public PlayerStats(List<Messager> messagers, List<Players> nodes) {
		this.messagers = messagers;
		this.nodes = nodes;
	}
	
	public List<Messager> getMessagers() {
		return messagers;
	}
	public List<Players> getNodes() {
		return nodes;
	}

}
