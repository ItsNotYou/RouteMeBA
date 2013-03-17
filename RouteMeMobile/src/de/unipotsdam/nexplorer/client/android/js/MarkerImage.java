package de.unipotsdam.nexplorer.client.android.js;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class MarkerImage {

	private int resourceId;
	private String imagePath;
	private Size size;
	private Point upperLeft;
	private Point lowerRight;

	public MarkerImage(String path, Size size, Point point, Point point2) {
		this.imagePath = path;
		this.size = size;
		this.upperLeft = point;
		this.lowerRight = point2;
	}

	public BitmapDescriptor create() {
		return BitmapDescriptorFactory.fromResource(resourceId);
	}
}
