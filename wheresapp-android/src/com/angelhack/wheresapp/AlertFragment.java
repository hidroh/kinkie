package com.angelhack.wheresapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

public class AlertFragment extends DialogFragment {
	
	/**
	 * Interface to interact with host activity.
	 */
	public interface AlertDialogListener {
		public void onPositiveClick();
		public void onNegativeClick();
	}
	
	/**
	 * Empty constructor required by framework.
	 */
	public AlertFragment() {
	}
	
	public static AlertFragment createInstance(String title, String message, boolean isCancelable, String positiveText, String negativeText) {
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putString("message", message);
		bundle.putBoolean("isCancelable", isCancelable);
		bundle.putString("positiveText", positiveText);
		bundle.putString("negativeText", negativeText);
		
		AlertFragment fragment = new AlertFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
	
	private AlertDialogListener mListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof AlertDialogListener) {
			mListener = (AlertDialogListener) activity;
		} else {
			throw new IllegalArgumentException("host activity must implement WarningDialogListener");
		}
	}
	
	@Override
	public void onDetach() {
		mListener = null;
		super.onDetach();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		String title = bundle.getString("title");
		String message = bundle.getString("message");
		boolean isCancelable = bundle.getBoolean("isCancelable");
		String positiveText = bundle.getString("positiveText");
		String negativeText = bundle.getString("negativeText");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setCancelable(isCancelable);
		if (!TextUtils.isEmpty(title)) {
			builder.setTitle(title);
		}
		if (!TextUtils.isEmpty(message)) {
			builder.setMessage(message);
		}
		
		return builder
				.setPositiveButton(TextUtils.isEmpty(positiveText) ? "Ok" : positiveText,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								getDialog().dismiss();
								if (mListener != null) {
									mListener.onPositiveClick();
								}
							}
						})
				.setNegativeButton(TextUtils.isEmpty(negativeText) ? "Cancel" : negativeText,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								getDialog().dismiss();
								if (mListener != null) {
									mListener.onNegativeClick();
								}
							}
						})
				.create();
	}

}
