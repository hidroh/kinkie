package com.angelhack.wheresapp;

import com.angelhack.wheresapp.AlertFragment.AlertDialogListener;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.Toast;

public class BaseActivity extends Activity implements AlertDialogListener {
	
    /**
	 * #########################################################################
	 * Utility methods show a standard {@link Dialog}
	 * #########################################################################
	 */
    public void showLoadingDialog(String message) {
    	FragmentManager fm = getFragmentManager();
    	FragmentTransaction ft = fm.beginTransaction();
    	Fragment fragment = fm.findFragmentByTag(LoadingFragment.class.getName());
    	if (fragment != null) {
			ft.remove(fragment);
		}

		LoadingFragment dialogFragment = LoadingFragment.createInstance(message, true);
		dialogFragment.show(ft, LoadingFragment.class.getName());
	}

	public void dismissLoadingDialog() {
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentByTag(LoadingFragment.class.getName());
		if (fragment != null) {
			fm.beginTransaction().remove(fragment).commitAllowingStateLoss();
		}
	}

	public void showAlertDialog(String title, String message) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		AlertFragment fragment = (AlertFragment) fm.findFragmentByTag(AlertFragment.class.getName());
		if (fragment != null) {
			ft.remove(fragment);
			ft.addToBackStack(null);
		}

		AlertFragment f = AlertFragment.createInstance(title, message, true, "", "");
		f.show(ft, AlertFragment.class.getName());
	}

	public void dismissAlertDialog() {
		FragmentManager fm = getFragmentManager();
		Fragment f = fm.findFragmentByTag(AlertFragment.class.getName());
		if (f != null) {
			if (f instanceof AlertFragment) {
				((AlertFragment) f).dismiss();
			}
		}
	}
	
	public void showToast(String message) {
		if (!TextUtils.isEmpty(message)) {
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onPositiveClick() {
	}

	@Override
	public void onNegativeClick() {
	}

}
