package net.clov3r.elig.processor;

import java.text.SimpleDateFormat;

import net.clov3r.elig.database.EliGContentProvider;
import net.clov3r.elig.database.EliGDatabase;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.User;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

public class TweetUserProcessor {

	private static final String LOG=TweetUserProcessor.class.getName();
	private Context context;
	
	
	public TweetUserProcessor(Context context){
		this.context=context;
	}
	
	public void parse(User user,ResponseList<Status> statuses) {
		
		ContentValues[] contentValues=new ContentValues[statuses.size()];
		
		ContentResolver cr=context.getContentResolver();
		
		for (int i=0;i<statuses.size();i++) {
			String tweetText=statuses.get(i).getText();
			Long tweetId=statuses.get(i).getId();
			Long tweetCreatedAt=statuses.get(i).getCreatedAt().getTime();
			
			ContentValues cv=new ContentValues();
			cv.put(EliGDatabase.TWEET_COL, tweetText);
			cv.put(EliGDatabase.TWEET_USER_COL, user.getName());
			cv.put(EliGDatabase.TWEET_USER_NAME_COL, user.getScreenName());
			cv.put(EliGDatabase.TWEET_ID_COL, tweetId);
			cv.put(EliGDatabase.TWEET_CREATED_AT_COL, tweetCreatedAt);
			contentValues[i]=cv;
		}
		cr.bulkInsert(EliGContentProvider.TWEET_USER_CONTENT_URI, contentValues);
		
		
	}

}
