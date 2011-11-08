package net.clov3r.elig.fragment;

import net.clov3r.elig.R;
import net.clov3r.elig.TweetAdapter;
import net.clov3r.elig.database.EliGContentProvider;
import net.clov3r.elig.database.EliGDatabase;
import net.clov3r.elig.util.Clov3rConstant;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * Render all the Tweets about the hashtag receiv into this activity
 * 
 * */

public class TwitterListFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	private final static String LOG = TwitterListFragment.class.getName();
	private SimpleCursorAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new TweetAdapter(getActivity(), R.layout.tweet_item, null,
				new String[] { EliGDatabase.TWEET_COL,
						EliGDatabase.TWEET_CREATED_AT_COL,
						EliGDatabase.TWEET_USER_COL }, new int[] {
						R.id.textTweet, R.id.textCreatedAt, R.id.textUser }, 0);
		setListAdapter(adapter);
		
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		getListView().setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View view, int position,long id) {
				CursorWrapper cursorWrapper=(CursorWrapper)adapter.getItemAtPosition(position);
				String tweet=cursorWrapper.getString(cursorWrapper.getColumnIndex(EliGDatabase.TWEET_COL));
				
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				
				sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Compartido con Eli-G ");
				
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, tweet);
				startActivity(Intent.createChooser(sharingIntent,getString(R.string.shared_with)));
				
				return true;
			}

	});
		
		getListView().setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
				CursorWrapper cursorWrapper=(CursorWrapper)adapter.getItemAtPosition(position);
				String twitterUsername=cursorWrapper.getString(cursorWrapper.getColumnIndex(EliGDatabase.TWEET_USER_COL));
				String twitterUserImage=cursorWrapper.getString(cursorWrapper.getColumnIndex(EliGDatabase.TWEET_IMAGE_URL_COL));
				
				Intent tweetDetailIntent = new Intent(Clov3rConstant.TWITTER_USERNAME_DETAIL);
				tweetDetailIntent.putExtra(Clov3rConstant.TWITTER_USERNAME_EXTRA, twitterUsername);
				tweetDetailIntent.putExtra(Clov3rConstant.TWITTER_USERIMAGE_EXTRA, twitterUserImage);
				
				startActivity(tweetDetailIntent);
				
				
			}

	});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public static TwitterListFragment newInstance() {
		return new TwitterListFragment();
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
		return new CursorLoader(getActivity(),EliGContentProvider.TWEET_CONTENT_URI, null, 
				null, null, EliGDatabase.TWEET_CREATED_AT_COL + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursor) {
		adapter.swapCursor(null);
	}

	public void refresh() {
		getLoaderManager().restartLoader(0, null, this);
		
	}

}
