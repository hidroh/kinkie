package com.angelhack.wheresapp;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.angelhack.wheresapp.ChatWindowsFragment.ChatWindowsCallbacks;
import com.angelhack.wheresapp.http.WebSocketService;
import com.angelhack.wheresapp.map.CommonMapFragment;
import com.angelhack.wheresapp.map.CommonMapFragment.MapFragmentListener;
import com.angelhack.wheresapp.map.InfoWindowData;
import com.angelhack.wheresapp.map.MapOptionsBuilder;
import com.angelhack.wheresapp.model.Message;
import com.google.android.gms.maps.model.MarkerOptions;

public class ChatMapActivity extends Activity implements MapFragmentListener, ChatWindowsCallbacks {
	private static final String TAG = ChatMapActivity.class.getSimpleName();

	// Save the tab which is opened.
	private static final String STATE_NAV = "STATE_NAV";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		openSocket();
		
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
		    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		    actionBar.setDisplayShowTitleEnabled(false);	
		}

		Tab tab = actionBar.newTab()
	            .setText("List View")
	            .setTabListener(new MyTabListener<ChatWindowsFragment>(this, "listview", ChatWindowsFragment.class, null));
	    actionBar.addTab(tab);

	    Bundle mapArguments = new Bundle();
	    mapArguments.putParcelable(CommonMapFragment.EXTRA_GMAP_OPTIONS, MapOptionsBuilder.createDefaultMapOptions());
	    tab = actionBar.newTab()
	        .setText("Map View")
	        .setTabListener(new MyTabListener<CommonMapFragment>(this, "mapview", CommonMapFragment.class, mapArguments));
	    actionBar.addTab(tab);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt(STATE_NAV, getActionBar().getSelectedNavigationIndex());
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_NAV));
	}
	
	/**
	 * #########################################################################
	 * Add a refresh button on action bar
	 * #########################################################################
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater= getMenuInflater();
		inflater.inflate(R.menu.menu_check, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_done) {
			closeSocket();
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * #########################################################################
	 * Implementation for methods in {@link MapFragmentListener}
	 * #########################################################################
	 */
	@Override
	public MarkerOptions getMarkerOptions(Object item) {
		return null;
	}

	@Override
	public InfoWindowData getInfoWindowData(Object item) {
		return null;
	}

	@Override
	public void onInfoWindowClick(Object item) {
	}

	/**
	 * #########################################################################
	 * Implementation for methods in {@link ChatWindowsCallbacks}
	 * #########################################################################
	 */
	@Override
	public void onSendMessageClick(String message) {
		Intent intent = new Intent(WebSocketService.ACTION_SEND_MESSAGE, null, this, WebSocketService.class);
		Message messageObj = new Message(this, message);
		intent.putExtra(WebSocketService.EXTRA_MESSAGE_JSON, messageObj);
		startService(intent);
	}
	
	private void openSocket() {
		Intent intent = new Intent(WebSocketService.ACTION_CONNECT_SOCKET, null, this, WebSocketService.class);
		startService(intent);
	}
	
	private void closeSocket() {
		Intent intent = new Intent(WebSocketService.ACTION_DISCONNECT_SOCKET, null, this, WebSocketService.class);
		startService(intent);
	}
	
}
