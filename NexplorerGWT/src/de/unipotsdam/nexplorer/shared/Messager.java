package de.unipotsdam.nexplorer.shared;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.gwt.user.client.rpc.IsSerializable;


public class Messager implements IsSerializable{

	@JsonProperty("id")
	public Long id;
	@JsonProperty("name")
	public String name;
	@JsonProperty("score")
	public Long score;
	
	public Messager(Long id, String name, Long score) {
		this.id = id;
		this.name = name;
		this.score = score;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getScore() {
		return score;
	}
	public void setScore(Long score) {
		this.score = score;
	}
	public Messager() {		
	}
}
