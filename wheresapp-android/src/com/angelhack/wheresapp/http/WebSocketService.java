package com.angelhack.wheresapp.http;

import java.net.URI;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.allproperty.android.sqlite.KinkyContract.Messages;
import com.allproperty.android.sqlite.KinkyContract.Users;
import com.angelhack.wheresapp.model.Message;
import com.angelhack.wheresapp.websocket.WebSocketClient;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class WebSocketService extends CustomService implements WebSocketClient.Listener {
	private static final String TAG = WebSocketService.class.getSimpleName();
	
	public static final String EXTRA_REQUEST_STATUS = "status";
	public static final String EXTRA_MESSAGE_JSON = "message";
	
	public static final String ACTION_CONNECT_SOCKET = "com.angelhack.wheresapp.action.ACTION_CONNECT_SOCKET";
	public static final String ACTION_DISCONNECT_SOCKET = "com.angelhack.wheresapp.action.ACTION_DISCONNECT_SOCKET";
	public static final String ACTION_SEND_MESSAGE = "com.angelhack.wheresapp.action.ACTION_SEND_MESSAGE";

	private static final int MAX_RETRY = 3;

	public WebSocketService() {
		super(TAG);
	}
	
	private WebSocketClient mSocketClient;
	private ContentResolver mResolver;
	private int mRetryCount = 0;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mResolver = getContentResolver();
		mSocketClient = new WebSocketClient(URI.create("ws://54.251.109.177"), this, null);
		mSocketClient.connect();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent(" + intent.toString() + ")");

		// Extract information of the request from supplied intent.
		String action = intent.getAction();
        Bundle extras = intent.getExtras();
        
        if (extras == null && action == null) {
            // Extras contain our ResultReceiver and data is our REST action.  
            // So, without these components we can't do anything useful.
            Log.d(TAG, "You did not pass extras or data with the Intent.");
            return;
        }
		
        if (ACTION_SEND_MESSAGE.equals(action)) {
        	Message message = extras.getParcelable(EXTRA_MESSAGE_JSON);
        	if (message != null) {
				String messageJson = new Gson().toJson(message, Message.class);
				Log.i(TAG, "message string to be sent to server: " + messageJson);
				if (mSocketClient.isConnected()) {
//					mSocketClient.send(messageJson);
					mSocketClient.send(new byte[] { (byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF });
					
				}
			}
        } else if (ACTION_CONNECT_SOCKET.equals(action)) {
            // Start the web socket client to start listen and open socket
        	if (mSocketClient != null && !mSocketClient.isConnected()) {
        		mSocketClient.connect();
        	}
        } else if (ACTION_DISCONNECT_SOCKET.equals(action)) {
        	if (mSocketClient != null) {
				stopSelf();
				Intent stopSuccessIntent = new Intent();
				stopSuccessIntent.putExtra(EXTRA_REQUEST_STATUS, true);
				sendBroadcast(stopSuccessIntent);
			}
        }
	}
	
	private void insertMessage(String message) {
        try {
        	Message gson = new Gson().fromJson(message, Message.class);
        	ContentValues cv = new ContentValues();
        	cv.put(Users.USER_ID, gson.user_id);
        	cv.put(Messages.MESSAGE, gson.message);
        	cv.put(Messages.MESSAGE_LAT, gson.latitude);
        	cv.put(Messages.MESSAGE_LNG, gson.longitude);
        	cv.put(Messages.MESSAGE_TIMESTAMP, gson.timestamp);
        	
			Uri uri = mResolver.insert(Messages.CONTENT_URI, cv);
			if (uri != null)
				Log.v(TAG, "inserting successfully with uri " + uri.toString());
		} catch (JsonSyntaxException e) {
			Log.w(TAG, String.format("Got string parsing errors! %s", e.toString()));
		}
	}

	/**
	 * #########################################################################
	 * Implementation for methods in {@link WebSocketClient#Listener}
	 * #########################################################################
	 */
    @Override
    public void onConnect() {
        Log.d(TAG, "Connected successfully, start listening!");
        mRetryCount = 0;
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, String.format("Got string message! %s", message));
        insertMessage(message);
    }

    @Override
    public void onMessage(byte[] data) {
        Log.d(TAG, String.format("Got binary message! %s", data.toString()));
    }

	@Override
    public void onDisconnect(int code, String reason) {
        Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
		if (mRetryCount < MAX_RETRY) {
			mSocketClient.connect();
			mRetryCount++;
		}
    }

    @Override
    public void onError(Exception error) {
        Log.e(TAG, "Error!", error);
    }
	
}
