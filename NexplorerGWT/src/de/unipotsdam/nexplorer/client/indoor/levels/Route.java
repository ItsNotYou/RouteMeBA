package de.unipotsdam.nexplorer.client.indoor.levels;

public class Route {

	private String source = null;
	private String destination = null;

	public Route() {
	}

	public Route(String source, String destination) {
		this.source = source;
		this.destination = destination;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}

		if (!this.getClass().equals(other.getClass())) {
			return false;
		}

		Route o = (Route) other;
		return this.getSource().equals(o.getSource()) && this.getDestination().equals(o.getDestination());
	}

	@Override
	public String toString() {
		return "Source: " + getSource() + ", destination: " + getDestination();
	}
}
