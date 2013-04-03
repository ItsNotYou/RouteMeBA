package de.unipotsdam.nexplorer.server.rest.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.unipotsdam.nexplorer.server.rest.JSONable;



@XmlRootElement
public class OK extends JSONable<OK> {
	
	/**
	 * muss mindestens eine Property haben, naja ...
	 */
	public String julian;		
	
	public OK() {			
	}			
}