package com.angelhack.wheresapp.map;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allpropertymedia.models.condodirectory.MRT;
import com.allpropertymedia.models.property.Property;
import com.angelhack.wheresapp.R;
import com.angelhack.wheresapp.model.PropertyListItem;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CommonMapFragment extends MapFragment implements
		LoaderCallbacks, InfoWindowAdapter, LocationSource, LocationListener {
	private static final String TAG = CommonMapFragment.class.getSimpleName();
	
	public static final String EXTRA_GMAP_OPTIONS = "mapOptions";
	public static final String EXTRA_DEFAULT_ZOOM = "defaultZoom";
	public static final String EXTRA_DEFAULT_BEARING = "defaultBearing";
	public static final String EXTRA_DEFAULT_TILT = "defaultTilt";
	
	InfoWindowData data;
	
	private static float DEFAULT_ZOOM = 16;
	private static float DEFAULT_BEARING = 0f;
	private static float DEFAULT_TILT = 0;
	
	public CommonMapFragment() {
	}
	
	private OnLocationChangedListener mapLocationListener;
	private LocationManager mLocationManager;

	private Criteria mCriteria = new Criteria();
	
	public interface MapFragmentListener {
		// Ask the hosting activity to return {@link MarkerOptions} for a generic data object
		public MarkerOptions getMarkerOptions(Object item);
		// Ask the hosting activity to return {@link InfoWindowData} for a generic data object
		public InfoWindowData getInfoWindowData(Object item);
		// Delegate marker click event to the hosting activity handle, pass along the data object tied to that marker
		public void onInfoWindowClick(Object item);
	}
	
	private MapFragmentListener mListener;
	
	/**
	 * Map to store markerId and data related to that marker (data object could
	 * be {@link Property}, {@link PropertyListItem}, {@link MRT},
	 * {@link School}, {@link NewLaunch})
	 */
	private Map<String, Object> markerDataMap = Collections.synchronizedMap(new HashMap<String, Object>());
	private InfoWindowData mMarkerData;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof MapFragmentListener) {
			mListener = (MapFragmentListener) activity;
		} else {
			throw new IllegalArgumentException("hosting activity must implement " + MapFragmentListener.class.getName());
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		Bundle bundle = getArguments();
		if (bundle.containsKey(EXTRA_DEFAULT_ZOOM))
			DEFAULT_ZOOM = bundle.getFloat(EXTRA_DEFAULT_ZOOM);
		if (bundle.containsKey(EXTRA_DEFAULT_BEARING))
			DEFAULT_BEARING = bundle.getFloat(EXTRA_DEFAULT_BEARING);
		if (bundle.containsKey(EXTRA_DEFAULT_TILT))
			DEFAULT_TILT = bundle.getFloat(EXTRA_DEFAULT_TILT);
	    
	    mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
	    mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstance) {
		super.onActivityCreated(savedInstance);
		GoogleMap googleMap = getMap();
		if (googleMap != null) {
			Bundle bundle = getArguments();
			GoogleMapOptions mapOptions = bundle.getParcelable(EXTRA_GMAP_OPTIONS);
			if (mapOptions != null) {
				UiSettings  settings = googleMap.getUiSettings();
				settings.setCompassEnabled(mapOptions.getCompassEnabled());
				settings.setRotateGesturesEnabled(mapOptions.getRotateGesturesEnabled());
				settings.setTiltGesturesEnabled(mapOptions.getTiltGesturesEnabled());
				
				googleMap.setMapType(mapOptions.getMapType());
				googleMap.setInfoWindowAdapter(this);
				googleMap.setMyLocationEnabled(true);
				googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
					@Override
					public boolean onMarkerClick(final Marker marker) {
						// Move camera to marker position
						moveToMarker(getMap(), marker);
						//show marker without image for responsiveness
						marker.showInfoWindow();
						return true;
					}
				});
				googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
					@Override
					public void onInfoWindowClick(Marker marker) {
						if (mListener != null) {
							Object item = markerDataMap.get(marker.getId());
							Log.v(TAG, "markerID: " + marker.getId() + " info window has been clicked");
							mListener.onInfoWindowClick(item);
						}
					}
				});
				googleMap.setLocationSource(this);
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mLocationManager.requestLocationUpdates(0L, 0.0f, mCriteria, this, null);
		GoogleMap map = getMap();
		if (map != null) {
			map.setLocationSource(this);	
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mLocationManager.removeUpdates(this);
		GoogleMap map = getMap();
		if (map != null) {
			map.setLocationSource(null);	
		}
	}

	/**
	 * #########################################################################
	 * Implementation for methods in {@link InfoWindowAdapter}
	 * #########################################################################
	 */
	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		// Inflate view to display data
		LayoutInflater mInflater = getActivity().getLayoutInflater();
		final View convertView = mInflater.inflate(R.layout.map_info_window, null);

		ImageView profilePic = (ImageView) convertView.findViewById(android.R.id.icon);
		TextView noOfMsg = (TextView) convertView.findViewById(android.R.id.text1);
		
		// Get the content to be displayed
		Object object = markerDataMap.get(marker.getId());
		if (mListener != null) {

			mMarkerData = mListener.getInfoWindowData(object);
			if (mMarkerData != null) {
				String imageUrl = mMarkerData.coverImageUrl;
				if (TextUtils.isEmpty(imageUrl)) {
					profilePic.setVisibility(View.GONE);
				} else {
					profilePic.setVisibility(View.VISIBLE);
				}
				if (TextUtils.isEmpty(mMarkerData.title)) {
					noOfMsg.setVisibility(View.GONE);	
				} else {
					noOfMsg.setText(mMarkerData.title);
				}
			} else {
				return null;
			}
		}
		
		return convertView;
	}

	/**
	 * #########################################################################
	 * Implementation for methods in {@link LocationSource}
	 * #########################################################################
	 */
	@Override
	public void activate(OnLocationChangedListener locationChangedListener) {
		mapLocationListener = locationChangedListener;
	}

	@Override
	public void deactivate() {
		mapLocationListener = null;
	}

	/**
	 * #########################################################################
	 * Implementation for methods in {@link LocationListener}
	 * #########################################################################
	 */
	@Override
	public void onLocationChanged(Location location) {
		if (mapLocationListener != null) {
			mapLocationListener.onLocationChanged(location);
			LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
			CameraUpdate cu = CameraUpdateFactory.newLatLng(latlng);
			GoogleMap map = getMap();
			if (map != null) {
				map.animateCamera(cu);	
			}
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * #########################################################################
	 * Implementation for methods in {@link LoaderCallbacks}
	 * #########################################################################
	 */
	@Override
	public void updateContent(List<Object> items) {
		Log.d(TAG, "updateContent(): number of items " + ((items == null) ? 0 : items.size()));
		addMarkersToMap(items);
	}
	
	/**
	 * Draw all the markers on the map
	 */
	private void addMarkersToMap(List<Object> items) {
		GoogleMap googleMap = getMap();
		if (googleMap != null && items != null && (items.size() > 0)) {
			Marker marker = null;
			for (Object item : items) {
				if (mListener != null && item != null) {
					// Get marker options
					MarkerOptions options = mListener.getMarkerOptions(item);
					// Add this marker options to map
					marker = googleMap.addMarker(options);
					// Pertain the marker id and related data object
					markerDataMap.put(marker.getId(), item);
				}
			}
			// Move the camera to the last marker
			moveToMarker(googleMap, marker);
		}
	}
	
	/**
	 * Move the camera to marker based on the position of marker in the list
	 */
	private void moveToMarker(GoogleMap googleMap, Marker marker) {
		if (marker != null) {
			CameraPosition cameraPosition = new CameraPosition(marker.getPosition(), DEFAULT_ZOOM, DEFAULT_TILT, DEFAULT_BEARING);
			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		}
	}

}
