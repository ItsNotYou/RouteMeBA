package de.unipotsdam.nexplorer.server.aodv;

import com.google.inject.Inject;

public class RREQFactory {

	private AodvFactory factory;

	@Inject
	public RREQFactory(AodvFactory factory) {
		this.factory = factory;
	}

	public RREQTemplate from(AodvNode aodvNode) {
		return new RREQTemplate(aodvNode, factory);
	}
}
