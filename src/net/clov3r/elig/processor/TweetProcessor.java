package net.clov3r.elig.processor;

import java.util.Date;
import java.util.List;

import net.clov3r.elig.database.EliGContentProvider;
import net.clov3r.elig.database.EliGDatabase;
import twitter4j.Tweet;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

public class TweetProcessor {

	private static final String LOG=TweetProcessor.class.getName();
	private Context context;
	
	
	public TweetProcessor(Context context){
		this.context=context;
	}
	
	public void parse(List<Tweet> tweets) {
		
		ContentValues[] contentValues=new ContentValues[tweets.size()];
		ContentResolver cr=context.getContentResolver();
		
		for (int i=0;i<tweets.size();i++) {
			Tweet tweet=tweets.get(i);
			String user=tweet.getFromUser();
			String tweetText=tweet.getText();
			String imgUrl=tweet.getProfileImageUrl();
			Date tweetCreatedAt=tweet.getCreatedAt();
			long tweetId=tweet.getId();
			
			
			
			ContentValues cv=new ContentValues();
			cv.put(EliGDatabase.TWEET_COL, tweetText);
			cv.put(EliGDatabase.TWEET_CREATED_AT_COL, tweetCreatedAt.getTime());
			cv.put(EliGDatabase.TWEET_IMAGE_URL_COL, imgUrl);
			cv.put(EliGDatabase.TWEET_USER_COL, user);
			cv.put(EliGDatabase.TWEET_ID_COL, tweetId);
			
			//cr.insert(EliGContentProvider.TWEET_CONTENT_URI, cv);
			contentValues[i]=cv;
		}
		//cr.insert(EliGContentProvider.TWEET_CONTENT_URI, cv);
		cr.bulkInsert(EliGContentProvider.TWEET_CONTENT_URI, contentValues);
		
		
	}

}
