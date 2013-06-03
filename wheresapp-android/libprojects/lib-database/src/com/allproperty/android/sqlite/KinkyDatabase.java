package com.allproperty.android.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.allproperty.android.database.BuildConfig;
import com.allproperty.android.sqlite.KinkyContract.Messages;
import com.allproperty.android.sqlite.KinkyContract.Users;

public class KinkyDatabase extends SQLiteOpenHelper {
	private static final String TAG = KinkyDatabase.class.getSimpleName();

    private static final String DATABASE_NAME = "angelhack.db";
    
	/**
	 * Everytime there is a change in the database tables such as column name
	 * changes, additional column... We need to increase this value of
	 * {@code DATABASE_VERSION}
	 */
    private static final int DATABASE_VERSION = 9;

	public KinkyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
    public KinkyDatabase(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	/** Enumerate all tables in PropertyGuruContract AgentNet database */
	interface Tables{
		String USERS = "users";
		String MESSAGES = "messages";
		String USERS_JOIN_MESSAGES = "users LEFT OUTER JOIN messages ON users.user_id=messages.user_id";
	}
	
	/** Perform database creation, if it's not existed */
	@Override
	public void onCreate(SQLiteDatabase db) {
		if (BuildConfig.DEBUG)
			Log.v(TAG, "creating sqlite database " + db.toString() + ", version " + db.getVersion());
		
		db.execSQL("CREATE TABLE " + Tables.USERS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Users.USER_ID + " TEXT NOT NULL,"
                + Users.USER_NAME + " TEXT,"
                + Users.USER_PHOTO_URL + " TEXT,"
                + Users.USER_GENDER + " TEXT,"
                + Users.USER_LATITUDE + " TEXT,"
                + Users.USER_LONGITUDE + " TEXT,"
                + "UNIQUE (" + Users.USER_ID + ") ON CONFLICT REPLACE)"
                );
		
		db.execSQL("CREATE TABLE " + Tables.MESSAGES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Messages.MESSAGE_ID + " TEXT,"
                + Users.USER_ID + " TEXT NOT NULL,"
                + Messages.MESSAGE + " TEXT,"
                + Messages.MESSAGE_TIMESTAMP + " TEXT,"
				+ Messages.MESSAGE_LAT + " TEXT,"
				+ Messages.MESSAGE_LNG + " TEXT,"
                + "UNIQUE (" + Messages.MESSAGE_ID + ") ON CONFLICT REPLACE)"
                );
	}

	/** Do database upgrade as necessary */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (BuildConfig.DEBUG)
			Log.v(TAG, "Upgrading database from " + oldVersion + " to " + newVersion);
        if (oldVersion != DATABASE_VERSION) {
			if (BuildConfig.DEBUG)
				Log.v(TAG, "Destroying old data if exists");

            db.execSQL("DROP TABLE IF EXISTS " + Tables.USERS);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.MESSAGES);
            onCreate(db);
        }
	}

}
