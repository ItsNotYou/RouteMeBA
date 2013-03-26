package de.unipotsdam.nexplorer.client.android.js;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class MarkerImage {

	private int resourceId;

	public MarkerImage(int resourceId) {
		this.resourceId = resourceId;
	}

	public BitmapDescriptor create() {
		return BitmapDescriptorFactory.fromResource(resourceId);
	}

	public float getU() {
		return (float) 0.5;
	}

	public float getV() {
		return (float) 0.5;
	}
}
