package com.angelhack.wheresapp.model;

import org.json.JSONException;
import org.json.JSONObject;

public class BaseObject {
	public static String getString(String key, JSONObject json) {
		try {
			if (!json.has(key) || json.getString(key).equals("null"))
				return null;

			return json.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static int getInt(String key, JSONObject json) {
		try {
			if (json.has(key) && json.getString(key) != null) {
				return Integer.parseInt(json.getString(key));
			} else {
				return 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public static double getDouble(String key, JSONObject json) {
		try {
			if (!json.has(key) || json.getString(key).equals("null"))
				return 0.0;

			return json.getDouble(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return 0.0;
	}
}