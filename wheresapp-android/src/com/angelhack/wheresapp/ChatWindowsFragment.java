package com.angelhack.wheresapp;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.allproperty.android.sqlite.KinkyContract.Messages;
import com.facebook.widget.ProfilePictureView;

public class ChatWindowsFragment extends ListFragment implements LoaderCallbacks<Cursor> {
	private static final String TAG = ChatWindowsFragment.class.getSimpleName();

	private static final int LOADER_MESSAGE = 100;
	
	public interface ChatWindowsCallbacks {
		public void onSendMessageClick(String message);
	}
	
	private ChatWindowsCallbacks mListener;
	
	private EditText mMessageEditText;
	private Button mSendButton;
	private ListView mListView;
	private MessageAdapter mListAdapter;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof ChatWindowsCallbacks) {
			mListener = (ChatWindowsCallbacks) activity;
		} else {
			throw new IllegalArgumentException("hosting activity must implement " + ChatWindowsCallbacks.class.getName());
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.chat_windows_fragment, container, false);
		
		mSendButton = (Button) view.findViewById(android.R.id.button1);
		mSendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mListener != null) {
					mListener.onSendMessageClick(mMessageEditText.getText().toString());
					// reset text and get rid of the keyboard
					mMessageEditText.clearComposingText();
					mMessageEditText.setText("");
				}
			}
		});
		
		mMessageEditText = (EditText) view.findViewById(android.R.id.edit);
		mListView = (ListView) view.findViewById(android.R.id.list);
		mListAdapter = new MessageAdapter(getActivity());
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mListView.setAdapter(mListAdapter);
		mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		mListView.setCacheColorHint(android.R.color.transparent);
		
		// Load messages
		getLoaderManager().initLoader(LOADER_MESSAGE, null, this);
	}
	
	/**
	 * #########################################################################
	 * Implementation for methods in {@link ArrayAdapter}
	 * #########################################################################
	 */
	class MessageAdapter extends CursorAdapter {
		private LayoutInflater inflater;

		public MessageAdapter(Context context) {
			super(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Holder holder = (Holder) view.getTag();
			if (holder != null) {
				holder.message.setText(cursor.getString(cursor.getColumnIndexOrThrow(Messages.MESSAGE)));
				holder.icon.setProfileId(cursor.getString(cursor.getColumnIndexOrThrow(Messages.USER_ID)));
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Get view reference
			View view = inflater.inflate(R.layout.list_item_message, parent, false);
			Holder holder = new Holder();
			holder.icon = (ProfilePictureView) view.findViewById(android.R.id.icon);
			holder.message = (TextView) view.findViewById(android.R.id.text1);
			view.setTag(holder);
			return view;
		}
		
	}
	
	class Holder {
		ProfilePictureView icon;
		TextView message;
	}

	/**
	 * #########################################################################
	 * Implementation for methods in {@link LoaderCallbacks}
	 * #########################################################################
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		if (id == LOADER_MESSAGE) {
			Log.v(TAG, "onCreateLoader(): has been called, start querying messages");
			return new CursorLoader(getActivity(), Messages.CONTENT_URI, null,
					null, null, Messages.DEFAULT_SORT);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (loader.getId() == LOADER_MESSAGE) {
			if (cursor != null) {
				mListAdapter.swapCursor(cursor);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (loader.getId() == LOADER_MESSAGE) {
			mListAdapter.swapCursor(null);
		}
	}

}
