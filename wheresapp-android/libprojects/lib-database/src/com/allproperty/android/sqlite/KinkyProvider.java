package com.allproperty.android.sqlite;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.allproperty.android.database.BuildConfig;
import com.allproperty.android.sqlite.KinkyContract.Messages;
import com.allproperty.android.sqlite.KinkyContract.Users;
import com.allproperty.android.sqlite.KinkyDatabase.Tables;

public class KinkyProvider extends ContentProvider {
	private static final String TAG = KinkyProvider.class.getSimpleName();

	/**
	 * Constants used to differentiate between URI requests
	 */
	private static final int USERS = 201;
	private static final int USERS_ID = 202;
	private static final int USERS_ID_MESSAGES = 203;

	private static final int MESSAGES = 301; // request for all messages
	private static final int MESSAGE_ID = 302; // request for message with
												// specified ID
		
	/**
	 * Subclass of this class need to replace this {@code CONTENT_AUTHORITY},
	 * in order to install multiple {@link ContentProvider} on the same phone.
	 */
	public static String CONTENT_AUTHORITY = "";
	public static Uri BASE_CONTENT_URI = Uri.EMPTY;
	
	public KinkyProvider(String contentAuthority) {
		CONTENT_AUTHORITY = contentAuthority;
		BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
		sUriMatcher = buildUriMatcher();
	}

	private KinkyDatabase mOpenHelper;
	private UriMatcher sUriMatcher;

	/**
	 * Build and return a {@link UriMatcher} that catches all {@link Uri}
	 * variations supported by this {@link ContentProvider}.
	 */
	private UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		matcher.addURI(CONTENT_AUTHORITY, "users", USERS);
		matcher.addURI(CONTENT_AUTHORITY, "users/*", USERS_ID);
		matcher.addURI(CONTENT_AUTHORITY, "users/*/messages", USERS_ID_MESSAGES);

		matcher.addURI(CONTENT_AUTHORITY, "messages", MESSAGES);
		matcher.addURI(CONTENT_AUTHORITY, "messages/*", MESSAGE_ID);
		
		return matcher;
	}

	/** {@inheritDoc} */
	@Override
	public boolean onCreate() {
		final Context context = getContext();
		mOpenHelper = new KinkyDatabase(context);
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case USERS:
			return Users.CONTENT_TYPE;
		case USERS_ID:
			return Users.CONTENT_ITEM_TYPE;
		case USERS_ID_MESSAGES:
			return Messages.CONTENT_TYPE;
		case MESSAGES:
			return Messages.CONTENT_TYPE;
		case MESSAGE_ID:
			return Messages.CONTENT_ITEM_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		if (BuildConfig.DEBUG)
			Log.v(TAG, "querying (uri=" + uri + ", proj="
				+ Arrays.toString(projection) + ", selection="
				+ selection + ", selectionArgs="
				+ Arrays.toString(selectionArgs) + ", sortOrder="
				+ sortOrder + ")");
		final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		final int match = sUriMatcher.match(uri);
		switch (match) {
		default:
			// Most cases are handled with simple SelectionBuilder
			final SelectionBuilder builder = buildExpandedSelection(uri, match);
			Cursor cursor = builder.where(selection, selectionArgs).query(db, projection, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			
			return cursor;
		}
	}

	/** {@inheritDoc} */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (BuildConfig.DEBUG)
			Log.v(TAG, "inserting (uri=[" + uri + "], values=[" + values.toString() + "])");
		
		final ContentResolver resolver = getContext().getContentResolver();
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case USERS: {
			db.insertOrThrow(Tables.USERS, null, values);
			String userId = values.getAsString(Users.USER_ID);
			Uri userUri = Users.buildUserUri(userId);
			resolver.notifyChange(userUri, null);
			return userUri;
		}
		case MESSAGES: {
			db.insertOrThrow(Tables.MESSAGES, null, values);
			String messageId = values.getAsString(Messages.MESSAGE_ID);
			Uri messageUri = Messages.buildMessageUri(messageId);
			resolver.notifyChange(messageUri, null);
			return messageUri;
		}
		default: {
			throw new UnsupportedOperationException("Cannot insert data into database. Unknown uri: " + uri);
		}
		}
	}

	/**
	 * {@inheritDoc} If number of {@link ContentValues} aka records is bigger
	 * than 1000, Android OS will throw error related to HeapWorker
	 */
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int noOfRecords = values.length;
		if(BuildConfig.DEBUG) Log.v(TAG, "inserting bulk of " + noOfRecords + " records to database (uri=[" + uri + "]");

		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case USERS: {
			db.beginTransaction();
			for (ContentValues value : values) {
				db.insertOrThrow(Tables.USERS, null, value);
			}
			db.setTransactionSuccessful();
			db.endTransaction();
			return noOfRecords;
		}
		case MESSAGES: {
			db.beginTransaction();
			for (ContentValues value : values) {
				db.insertOrThrow(Tables.MESSAGES, null, value);
			}
			db.setTransactionSuccessful();
			db.endTransaction();
			return noOfRecords;
		}
		default: {
			throw new UnsupportedOperationException("Cannot insert bulk of data into database. Unknown uri: " + uri);
		}
		}
	}

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        final ContentResolver resolver = getContext().getContentResolver();
        final int numOperations = operations.size();
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        
        if (BuildConfig.DEBUG) Log.d(TAG, "applying batch of ContentProviderOperation: " + operations.size());
        
        db.beginTransaction();
        try {
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                ContentProviderOperation operation = operations.get(i);
				results[i] = operation.apply(this, results, i);
				if (results[i] != null) {
					if (results[i].uri != null) {
						resolver.notifyChange(results[i].uri, null);
						if (BuildConfig.DEBUG) Log.v(TAG, "ContentProviderOperation applied successful, uri to be notified: " + results[i].uri);
					} else if (results[i].count >= 0) {
						if (BuildConfig.DEBUG) Log.v(TAG, "ContentProviderOperation applied successful, no of row affected: " + results[i].count);
					}
				}
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

	/** {@inheritDoc} */
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		if (BuildConfig.DEBUG)
			Log.v(TAG, "updating (uri=[" + uri + "], values=[" + values.toString()
					+ "], selection=[" + selection
					+ "], selectionArgs=["
					+ Arrays.toString(selectionArgs) + "])");

		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final SelectionBuilder builder = buildSimpleSelection(uri);
		final int noOfRowsAffected = builder.where(selection, selectionArgs).update(db, values);
		
		if (noOfRowsAffected > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		return noOfRowsAffected;
	}

	/** {@inheritDoc} */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if (BuildConfig.DEBUG)
			Log.v(TAG, "deleting (uri=[" + uri + "], selection=[" + selection
					+ "], selectionArgs=[" + Arrays.toString(selectionArgs)
					+ "])");
		
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final SelectionBuilder builder = buildSimpleSelection(uri);
		final int noOfRowsAffected = builder.where(selection, selectionArgs).delete(db);
		
		if (noOfRowsAffected > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		return noOfRowsAffected;
	}

	/**
	 * Build a simple {@link SelectionBuilder} to match the requested
	 * {@link Uri}. This is usually enough to support {@link #insert},
	 * {@link #update}, and {@link #delete} operations.
	 */
	private SelectionBuilder buildSimpleSelection(Uri uri) {
		final SelectionBuilder builder = new SelectionBuilder();
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case USERS: {
			return builder.table(Tables.USERS);
		}
		case USERS_ID: {
			final String userId = uri.getPathSegments().get(1);
			return builder.table(Tables.USERS).where(Users.USER_ID + "=?",
					userId);
		}
		case MESSAGES: {
			return builder.table(Tables.MESSAGES);
		}
		case MESSAGE_ID: {
			final String messageId = uri.getPathSegments().get(1);
			return builder.table(Tables.MESSAGES).where(
					Messages.MESSAGE_ID + "=?", messageId);
		}
		default: {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		}
	}

	/**
	 * Build an advanced {@link SelectionBuilder} to match the requested
	 * {@link Uri}. This is usually only used by {@link #query}, since it
	 * performs table joins useful for {@link Cursor} data.
	 */
	private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
		final SelectionBuilder builder = new SelectionBuilder();
		switch (match) {
		case USERS: {
			return builder.table(Tables.USERS);
		}
		case USERS_ID: {
			final String userId = Users.getUserId(uri);
			return builder.table(Tables.USERS).where(Users.USER_ID + "=?",
					userId);
		}
		case USERS_ID_MESSAGES: {
			final String userId = Users.getUserId(uri);
			return builder.table(Tables.USERS_JOIN_MESSAGES)
					.mapToTable(Messages._ID, Tables.MESSAGES)
					.mapToTable(Messages.USER_ID, Tables.MESSAGES)
					.where(Qualified.MESSAGE_USER_ID + "=?", userId);
		}
		case MESSAGES: {
			return builder.table(Tables.MESSAGES);
		}
		case MESSAGE_ID: {
			final String messageId = Messages.getMessageId(uri);
			return builder.table(Tables.MESSAGES).where(
					Messages.MESSAGE_ID + "=?", messageId);
		}
		default: {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		}
	}

	/**
	 * {@link KinkyContract} fields that are fully qualified with a specific
	 * parent {@link Tables}. Used when needed to work around SQL ambiguity.
	 */
	private interface Qualified {
		String MESSAGE_USER_ID = Tables.MESSAGES + "." + Messages.USER_ID;
	}
}
