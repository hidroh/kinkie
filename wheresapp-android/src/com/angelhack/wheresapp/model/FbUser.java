package com.angelhack.wheresapp.model;

import com.facebook.model.GraphUser;

import android.os.Parcel;
import android.os.Parcelable;

public class FbUser implements Parcelable {

	public static final Parcelable.Creator<FbUser> CREATOR = new Parcelable.Creator<FbUser>() {
		public FbUser createFromParcel(Parcel source) {
			return new FbUser(source);
		}

		public FbUser[] newArray(int size) {
			return new FbUser[size];
		}
	};
	
	public int id;
	public String user_id;
	public String fullname;
	public String gender;
	public String latitude;
	public String longitude;

	public FbUser(Parcel source) {
		// TODO Auto-generated constructor stub
		id = source.readInt();
		user_id = source.readString();
		fullname = source.readString();
		gender = source.readString();
		latitude = source.readString();
		longitude = source.readString();
	}

	public FbUser(GraphUser user) {
		this.user_id = user.getId();
		this.fullname= user.getName();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(id);
		dest.writeString(user_id);
		dest.writeString(fullname);
		dest.writeString(gender);
		dest.writeString(latitude);
		dest.writeString(longitude);
	}

}
