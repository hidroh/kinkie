package com.angelhack.wheresapp.model;

import com.angelhack.wheresapp.preference.GlobalConstants;
import com.angelhack.wheresapp.preference.PreferenceHelper;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable {

	public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
		public Message createFromParcel(Parcel source) {
			return new Message(source);
		}

		public Message[] newArray(int size) {
			return new Message[size];
		}
	};
	
	public String user_id;
	public String type = "message";
	public String message;
	public String timestamp;
	public String latitude;
	public String longitude;
	
	public Message(Context context, String message) {
		this.user_id = PreferenceHelper.loadStr(context, GlobalConstants.PREF_USER_ID);
		this.message = message;
	}

	public Message(Parcel source) {
		// TODO Auto-generated constructor stub
		user_id = source.readString();
		type = source.readString();
		message = source.readString();
		timestamp = source.readString();
		latitude = source.readString();
		longitude = source.readString();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return user_id.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(user_id);
		dest.writeString(type);
		dest.writeString(message);
		dest.writeString(timestamp);
		dest.writeString(latitude);
		dest.writeString(longitude);
	}

}
