package com.angelhack.wheresapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class LoggedOutFragment extends Fragment {
	
	public interface LoggedOutCallbacks {
		public void onFacebookClick();
		public void onTwitterClick();
	}
	
	private LoggedOutCallbacks mListener;

	private Button facebookButton;
	private Button twitterButton;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof LoggedOutCallbacks) {
			mListener = (LoggedOutCallbacks) activity;
		} else {
			throw new IllegalArgumentException("hosting activity must implement " + LoggedOutCallbacks.class.getName());
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.logged_out_fragment, container, false);

		facebookButton = (Button) view.findViewById(android.R.id.button1);
		facebookButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onFacebookClick();
				}
			}
		});

		twitterButton = (Button) view.findViewById(android.R.id.button2);
		twitterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onTwitterClick();
				}
			}
		});
		return view;
	}

}
