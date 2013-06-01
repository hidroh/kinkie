package com.angelhack.wheresapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.angelhack.wheresapp.LoggedInFragment.LoggedInCallbacks;
import com.angelhack.wheresapp.LoggedOutFragment.LoggedOutCallbacks;
import com.angelhack.wheresapp.model.FbUser;
import com.angelhack.wheresapp.preference.GlobalConstants;
import com.angelhack.wheresapp.preference.PreferenceHelper;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.analytics.tracking.android.Log;

public class HomeActivity extends BaseActivity implements LoggedInCallbacks, LoggedOutCallbacks {

	private FbUser mFbUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		showLoggedOutScreen();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	private void showLoggedOutScreen() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = Fragment.instantiate(this, LoggedOutFragment.class.getName());
		ft.replace(R.id.content_container, fragment, LoggedOutFragment.class.getName());
		ft.commit();
	}
	
	private void showLoggedInScreen() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Bundle bundle = new Bundle();
		bundle.putParcelable("user", mFbUser);
		Fragment fragment = Fragment.instantiate(this, LoggedInFragment.class.getName(), bundle);
		ft.replace(R.id.content_container, fragment, LoggedInFragment.class.getName());
		ft.commit();
	}
	
	private void showLoadingScreen() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = LoadingFragment.createInstance("Now loading...", false);
		ft.replace(R.id.content_container, fragment, LoadingFragment.class.getName());
		ft.commit();
	}

	private void openChatMap() {
		Intent intent = new Intent(HomeActivity.this, ChatMapActivity.class);
		intent.putExtra("user", mFbUser);
		startActivity(intent);
	}

	@Override
	public void onJoinClick() {
		openChatMap();
	}

	// start Facebook Login
	@Override
	public void onFacebookClick() {
		showLoadingScreen();
		Session.openActiveSession(HomeActivity.this, true, new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {
					fetchUserInfo(session);
				}
			}
		});
	}
	
	private void fetchUserInfo(Session session) {
		// make request to the /me API
		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (user != null) {
					Log.v("Get user info successfully! " + user.getName());
					
					// Save user credentials
					mFbUser = new FbUser(user);
					PreferenceHelper.saveStr(HomeActivity.this, GlobalConstants.PREF_USER_ID, mFbUser.user_id);
					showLoggedInScreen();
				}
			}
		});
	}

	// start Twitter Login
	@Override
	public void onTwitterClick() {
	}

}
