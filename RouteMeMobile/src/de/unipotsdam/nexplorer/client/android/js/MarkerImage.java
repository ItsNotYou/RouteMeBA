package de.unipotsdam.nexplorer.client.android.js;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class MarkerImage {

	private int resourceId;
	private Size size;
	private Point upperLeft;
	private Point lowerRight;

	public MarkerImage(int resourceId, Size size, Point point, Point point2) {
		this.resourceId = resourceId;
		this.size = size;
		this.upperLeft = point;
		this.lowerRight = point2;
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
