package de.unipotsdam.nexplorer.client.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import de.unipotsdam.nexplorer.client.android.sensors.MapRotator;
import de.unipotsdam.nexplorer.client.android.support.MapInitializer;

public class RotatingMapFragment extends Fragment {

	private GoogleMap googleMap;
	private MapRotator map;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.fragment_rotating_map, container, false);

		MapInitializer mapInit = new MapInitializer(getFragmentManager(), R.id.googleMap);
		googleMap = mapInit.initMap();
		if (googleMap == null) {
			Toast.makeText(getActivity(), "Map-Service ist nicht installiert", Toast.LENGTH_LONG).show();
			return null;
		}

		map = new MapRotator(getActivity(), googleMap);
		map.setUpMapIfNeeded(true);

		return result;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public GoogleMap getGoogleMap() {
		return googleMap;
	}

	public MapRotator getMapRotator() {
		return map;
	}
}
