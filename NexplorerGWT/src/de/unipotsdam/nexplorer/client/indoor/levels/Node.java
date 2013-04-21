package de.unipotsdam.nexplorer.client.indoor.levels;

public class Node {

	private final String id;

	public Node(int id) {
		this.id = Integer.toString(id);
	}

	public String getId() {
		return this.id;
	}
}
