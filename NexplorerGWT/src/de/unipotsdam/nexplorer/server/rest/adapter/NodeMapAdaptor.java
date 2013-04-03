package de.unipotsdam.nexplorer.server.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.unipotsdam.nexplorer.shared.NodeMap;

public class NodeMapAdaptor extends XmlAdapter<NodeMap, NodeMap> {

	@Override
	public NodeMap unmarshal(NodeMap v) throws Exception {
		return v;
	}

	@Override
	public NodeMap marshal(NodeMap v) throws Exception {
		return v;
	}
}
