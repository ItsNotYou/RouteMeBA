package de.unipotsdam.nexplorer.tools.dummyplayer;

public class Ping {

	private int nodeId;

	public Ping(int id) {
		this.nodeId = id;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
}
