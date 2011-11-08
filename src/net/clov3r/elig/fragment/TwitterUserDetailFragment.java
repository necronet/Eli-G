package net.clov3r.elig.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.clov3r.elig.R;
import net.clov3r.elig.ServiceHelper;
import net.clov3r.elig.database.EliGContentProvider;
import net.clov3r.elig.database.EliGDatabase;
import net.clov3r.elig.processor.EliGActions;
import net.clov3r.elig.util.Clov3rConstant;
import net.clov3r.elig.util.ImageManager;
import net.clov3r.elig.util.ToolKit;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TwitterUserDetailFragment extends ListFragment implements LoaderCallbacks<Cursor>{

	private static final String LOG=TwitterUserDetailFragment.class.getName();
	private SimpleCursorAdapter adapter;
	private TextView textUser;
	private String username;
	private String imageUrl;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View view=inflater.inflate(R.layout.tweet_user_detail, container);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		imageUrl=getActivity().getIntent().getStringExtra(Clov3rConstant.TWITTER_USERIMAGE_EXTRA);
		
		ImageView imageView=(ImageView)getView().findViewById(R.id.imageUserView);
		imageView.setTag(imageUrl);
		ImageManager imageManager=new ImageManager(getActivity());
		imageManager.displayImage(imageUrl, imageView);
		
		username=getActivity().getIntent().getStringExtra(Clov3rConstant.TWITTER_USERNAME_EXTRA);
		
		adapter=new TweetUserDetailAdapter(getActivity(), R.layout.tweet_user_item, null,
				new String[] { EliGDatabase.TWEET_COL,
			EliGDatabase.TWEET_CREATED_AT_COL}, new int[] {
			R.id.textTweet, R.id.textCreatedAt}, 0);
		
		TextView textUsername=(TextView)getView().findViewById(R.id.textUsername);
		textUsername.setText(username);
		textUser=(TextView)getView().findViewById(R.id.textUser);
		
		setListAdapter(adapter);
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(),
				EliGContentProvider.TWEET_USER_CONTENT_URI, null, EliGDatabase.TWEET_USER_NAME_COL+"=?", new String[]{username}, EliGDatabase.TWEET_CREATED_AT_COL + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
		if(data.getCount()==0){//no data called the ServiceHelper
			Bundle bundle=new Bundle();
			bundle.putString(Clov3rConstant.TWITTER_USERNAME_EXTRA, username);
			ServiceHelper.getInstance(getActivity()).startService(EliGActions.TWITTER_DETAIL_LIST_ACTION,bundle);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	public void refresh(String username, String user) {
		getLoaderManager().restartLoader(0, null, this);
		textUser=(TextView)getView().findViewById(R.id.textUser);
		textUser.setText(user);
		TextView textUsername=(TextView)getView().findViewById(R.id.textUsername);
		textUsername.setText(username);
	}
	
	
	
	
	/**
	 *  TweetUserDetailAdapter inherit elements from SimpleCursorAdapter
	 *  but customized the view on the TextComponent
	 * */
	
	

public class TweetUserDetailAdapter extends SimpleCursorAdapter {

	public TweetUserDetailAdapter(Context context, int resource_layout,
			Cursor cursor, String[] from, int[] to, int flag) {
		super(context,resource_layout,cursor,from,to,flag);
	
	}
	
	

	public View getView(int position, View convertView, ViewGroup group){
		
		if(convertView==null){
			convertView=super.getView(position, convertView, group);
		}
		
		CursorWrapper tweetUserDetailCursor=(CursorWrapper)getItem(position);
		
		if(tweetUserDetailCursor!=null){
			TextView textTweet=(TextView)convertView.findViewById(R.id.textTweet);
			Linkify.addLinks(textTweet, Linkify.WEB_URLS);
			textTweet.setMovementMethod(null);
			
			TextView textDate=(TextView)convertView.findViewById(R.id.textCreatedAt);
			Long time=tweetUserDetailCursor.getLong(tweetUserDetailCursor.getColumnIndex(EliGDatabase.TWEET_CREATED_AT_COL));
			
			try {
				textDate.setText(ToolKit.getFriendlyTime(time));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			textUser.setText(tweetUserDetailCursor.getString(tweetUserDetailCursor.getColumnIndex(EliGDatabase.TWEET_USER_COL)));
		}
		return convertView;		
	}
	
	
	
	
}

}
