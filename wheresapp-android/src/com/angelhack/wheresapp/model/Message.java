package com.angelhack.wheresapp.model;

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
	
	public int id;
	public int user_id;
	public String message;
	public String timestamp;
	public String latitude;
	public String longitude;

	public Message(Parcel source) {
		// TODO Auto-generated constructor stub
		id = source.readInt();
		user_id = source.readInt();
		message = source.readString();
		timestamp = source.readString();
		latitude = source.readString();
		longitude = source.readString();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return user_id;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(id);
		dest.writeInt(user_id);
		dest.writeString(message);
		dest.writeString(timestamp);
		dest.writeString(latitude);
		dest.writeString(longitude);
	}

}
