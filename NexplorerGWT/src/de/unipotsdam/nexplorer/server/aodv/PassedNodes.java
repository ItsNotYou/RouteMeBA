package de.unipotsdam.nexplorer.server.aodv;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PassedNodes extends LinkedList<Long> {

	private static final long serialVersionUID = -1295589753752472515L;

	public PassedNodes(String passedNodes) {
		if (passedNodes != null) {
			List<String> result = Arrays.asList(passedNodes.split(":"));
			for (String node : result) {
				add(Long.parseLong(node));
			}
		}
	}

	public String persistable() {
		String result = get(0).toString();
		for (int count = 1; count < size(); count++) {
			result += ":" + get(count);
		}
		return result;
	}
}
