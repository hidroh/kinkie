package com.angelhack.wheresapp.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;

public class MapOptionsBuilder {
	
	public static GoogleMapOptions createDefaultMapOptions() {

		// Initialize this activity with a map and an info fragment.
		GoogleMapOptions options = new GoogleMapOptions()
				.mapType(GoogleMap.MAP_TYPE_NORMAL)
				.compassEnabled(true)
				.rotateGesturesEnabled(false)
				.tiltGesturesEnabled(false)
				.useViewLifecycleInFragment(true)
				.zoomControlsEnabled(true)
				.zoomGesturesEnabled(true);
		
		return options;
	}

}
