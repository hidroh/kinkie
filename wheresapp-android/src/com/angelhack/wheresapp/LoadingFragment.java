package com.angelhack.wheresapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoadingFragment extends DialogFragment {

	public LoadingFragment() {
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.loading_fragment, container, false);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		String message = bundle.getString("message");
		boolean isCancelable = bundle.getBoolean("isCancelable");

		ProgressDialog dialog = new ProgressDialog(getActivity());
		if (!TextUtils.isEmpty(message))
			dialog.setMessage(message);
		dialog.setCancelable(isCancelable);
		
		return dialog;
	}

	public static LoadingFragment createInstance(String message, boolean isCancelable) {
		Bundle bundle = new Bundle();
		bundle.putString("message", message);
		bundle.putBoolean("isCancelable", isCancelable);
		
		LoadingFragment fragment = new LoadingFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
}
