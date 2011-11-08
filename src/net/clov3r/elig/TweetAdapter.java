package net.clov3r.elig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.clov3r.elig.database.EliGDatabase;
import net.clov3r.elig.util.ImageManager;
import net.clov3r.elig.util.ToolKit;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetAdapter extends SimpleCursorAdapter {

	//private static final String LOG=TweetAdapter.class.getName();
	private ImageManager imageManager;
	
	private LayoutInflater inflater;
	
	public TweetAdapter(Context context, int resource_layout,
			Cursor cursor, String[] from, int[] to, int flag) {
		super(context,resource_layout,cursor,from,to,flag);
		inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageManager=new ImageManager(context);
		
	}
	
	

	public View getView(int position, View convertView, ViewGroup group){
		
		if(convertView==null){
			convertView=inflater.inflate(R.layout.tweet_item,null);
		}
		CursorWrapper tweetCursor=(CursorWrapper)getItem(position);
		
		if(tweetCursor!=null){
			
			TextView textTweet=(TextView)convertView.findViewById(R.id.textTweet);
			textTweet.setText(tweetCursor.getString(tweetCursor.getColumnIndex(EliGDatabase.TWEET_COL)));
			Linkify.addLinks(textTweet, Linkify.WEB_URLS);
			textTweet.setMovementMethod(null);
			
			TextView textUser=(TextView)convertView.findViewById(R.id.textUser);
			textUser.setText(tweetCursor.getString(tweetCursor.getColumnIndex(EliGDatabase.TWEET_USER_COL)));
			
			TextView textDate=(TextView)convertView.findViewById(R.id.textCreatedAt);
			Long time=tweetCursor.getLong(tweetCursor.getColumnIndex(EliGDatabase.TWEET_CREATED_AT_COL));
			
			try {
				textDate.setText(ToolKit.getFriendlyTime(time));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ImageView imageUser = (ImageView)convertView.findViewById(R.id.imageTweeter);
			String url=tweetCursor.getString(tweetCursor.getColumnIndex(EliGDatabase.TWEET_IMAGE_URL_COL));
			imageUser.setTag(url);
			
			imageManager.displayImage(url, imageUser,true);
			
		}
		return convertView;		
	}
	
	
	
	
}
