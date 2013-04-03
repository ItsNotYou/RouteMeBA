package de.unipotsdam.nexplorer.server.aodv;


public class RREQTemplate {

	private AodvNode source;
	private AodvFactory factory;

	public RREQTemplate(AodvNode source, AodvFactory factory) {
		this.source = source;
		this.factory = factory;
	}

	public RREQDestination to(long destination) {
		return factory.create(source, destination);
	}
}
