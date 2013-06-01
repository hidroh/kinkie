package com.angelhack.wheresapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.angelhack.wheresapp.model.FbUser;
import com.facebook.widget.ProfilePictureView;

public class LoggedInFragment extends Fragment {
	private ProfilePictureView profilePictureView;
	private TextView userNameView;
	private Button joinButton;
	
	public interface LoggedInCallbacks {
		public void onJoinClick();
	}
	
	private LoggedInCallbacks mListener;
	private FbUser mFbUser;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof LoggedInCallbacks) {
			mListener = (LoggedInCallbacks) activity;
		} else {
			throw new IllegalArgumentException("hosting activity must implement " + LoggedInCallbacks.class.getName());
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		mFbUser = bundle.getParcelable("user");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.logged_in_fragment, container, false);

		// Find the user's profile picture custom view
		profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
		profilePictureView.setCropped(true);
		profilePictureView.setProfileId(mFbUser.user_id);

		// Find the user's name view
		userNameView = (TextView) view.findViewById(R.id.selection_user_name);
		userNameView.setText(String.format("Welcome comrade %s, jump in and chat now", mFbUser.fullname));
		
		// join button
		joinButton = (Button) view.findViewById(android.R.id.button3);
		joinButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mListener != null) mListener.onJoinClick();
			}
		});
		return view;
	}
	
}
