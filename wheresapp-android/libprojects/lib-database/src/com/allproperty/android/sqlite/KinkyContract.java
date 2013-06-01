package com.allproperty.android.sqlite;

import android.net.Uri;
import android.provider.BaseColumns;

public class KinkyContract {

	private static final String PATH_USERS = "users";
	private static final String PATH_MESSAGES = "messages";

	interface UserColumns {
		String USER_ID = "user_id";
		String USER_NAME = "user_fullname";
		String USER_GENDER = "user_gender";
		String USER_PHOTO_URL = "user_photo_url";
		String USER_LATITUDE = "user_lat";
		String USER_LONGITUDE = "user_lng";
	}

	interface MessagesColumns {
		String MESSAGE_ID = "id";
		String MESSAGE = "reason";
		String MESSAGE_TIMESTAMP = "date";
		String MESSAGE_LAT = "contact_location";
		String MESSAGE_LNG = "contact_method";
	}

	/**
	 * Users table contains all users'information
	 */
	public static class Users implements UserColumns, BaseColumns {
		public static final Uri CONTENT_URI = KinkyProvider.BASE_CONTENT_URI
				.buildUpon().appendPath(PATH_USERS).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.angelhack.user";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.angelhack.user";

		/** Default "ORDER BY" clause. */
		public static final String DEFAULT_SORT = UserColumns.USER_ID
				+ " COLLATE NOCASE ASC";

		/** Build {@link Uri} for requested {@link #USER_ID}. */
		public static Uri buildUserUri(String userId) {
			return CONTENT_URI.buildUpon().appendPath(userId).build();
		}

		/** Read {@link #USER_ID} from {@link Users} {@link Uri}. */
		public static String getUserId(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		/**
		 * Build {@link Uri} that references any {@link Messages} associated
		 * with the requested {@link #USER_ID}.
		 */
		public static Uri buildMessagesUri(String userId) {
			return CONTENT_URI.buildUpon().appendPath(userId)
					.appendPath(PATH_MESSAGES).build();
		}
		
	}

	/**
	 * Messages table contains all enquires sent to agents.
	 */
	public static class Messages implements MessagesColumns, UserColumns, BaseColumns {

		public static final Uri CONTENT_URI = KinkyProvider.BASE_CONTENT_URI
				.buildUpon().appendPath(PATH_MESSAGES).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.angelhack.message";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.angelhack.message";

		/** Default "ORDER BY" clause. */
		public static final String DEFAULT_SORT = MessagesColumns.MESSAGE_TIMESTAMP
				+ " COLLATE NOCASE DESC";

		/** Build {@link Uri} for requested {@link #MESSAGE_ID}. */
		public static Uri buildMessageUri(String messageId) {
			return Uri.withAppendedPath(CONTENT_URI, messageId);
		}

		/** Return {@link #MESSAGE_ID} for given {@link Uri} . */
		public static String getMessageId(Uri uri) {
			return uri.getPathSegments().get(1);
		}

	}

}
