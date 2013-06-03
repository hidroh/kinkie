package com.angelhack.wheresapp.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class PreferenceHelper {

	public static void saveStr(final Context context, String key, String value) {
		new AsyncTask<String, Void, Void>() {
			@Override
			protected Void doInBackground(String... params) {
				SharedPreferences.Editor editor = context.getSharedPreferences(GlobalConstants.PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
				editor.putString(params[0], params[1]);
				editor.commit();
				return null;
			}
		}.execute(key, value);
	}

	public static void saveLong(final Context context, String key, long value) {
		new AsyncTask<String, Void, Void>() {
			@Override
			protected Void doInBackground(String... params) {
				SharedPreferences.Editor editor = context.getSharedPreferences(GlobalConstants.PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
				editor.putLong(params[0], Long.valueOf(params[1]));
				editor.commit();
				return null;
			}
		}.execute(key, String.valueOf(value));
	}
	
	public static void saveBoolean(final Context context, String key, boolean value) {
		new AsyncTask<String, Void, Void>() {
			@Override
			protected Void doInBackground(String... params) {
				SharedPreferences.Editor editor = context.getSharedPreferences(GlobalConstants.PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
				editor.putBoolean(params[0], Boolean.valueOf(params[1]));
				editor.commit();
				return null;
			}
		}.execute(key, String.valueOf(value));
	}

	public static String loadStr(Context context, String key) {
		return loadStr(context, GlobalConstants.PREF_FILE_NAME, key);
	}

	public static String loadStr(Context context, String preferenceFileName, String key) {
		SharedPreferences preferences = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE);
		return preferences.getString(key, null);
	}

	public static long loadLong(Context context, String key, long defaultValue) {
		SharedPreferences preferences = context.getSharedPreferences(GlobalConstants.PREF_FILE_NAME, Context.MODE_PRIVATE);
		return preferences.getLong(key, defaultValue);
	}

	public static boolean loadBool(Context context, String key, boolean defaultValue) {
		SharedPreferences preferences = context.getSharedPreferences(GlobalConstants.PREF_FILE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(key, defaultValue);
	}

}
