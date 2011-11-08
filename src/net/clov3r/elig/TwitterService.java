package net.clov3r.elig;

import net.clov3r.elig.database.EliGContentProvider;
import net.clov3r.elig.database.EliGDatabase;
import net.clov3r.elig.processor.EliGActions;
import net.clov3r.elig.processor.TweetProcessor;
import net.clov3r.elig.processor.TweetUserProcessor;
import net.clov3r.elig.util.Clov3rConstant;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * This service is in charge of developing the http request on the Twitter API
 * */
public class TwitterService extends IntentService {

	private static final String LOG = TwitterService.class.getName();

	private static final String CONSUMER_KEY = "YOUR_KEY";
	private static final String CONSUMER_SECRET = "YOUR_SECRET";
	private static final String ACCESS_TOKEN = "YOUR_ACCESS_TOKEN";
	private static final String ACCESS_TOKEN_SECRET = "YOUR_ACCESS_TOKEN_SECRET";
	private ConfigurationBuilder cb;
	private TwitterFactory tf;
	private Twitter twitter;

	public TwitterService() {
		super("TwitterService");

		cb = new ConfigurationBuilder();

		cb.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY)
				.setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		tf = new TwitterFactory(cb.build());
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(LOG, "Twitter service started");

		Intent broadCastIntent = new Intent();

		String action = intent.getStringExtra("action");
		//TODO: repair this
		if (action == null)
			action = EliGActions.TWITTER_LIST_ACTION;
		
		if (action.equals(EliGActions.TWITTER_LIST_ACTION)) {
			listTwitter();
		} else if (action.equals(EliGActions.TWITTER_DETAIL_LIST_ACTION)) {
			detailTwitter(
					intent.getExtras().getString(
							Clov3rConstant.TWITTER_USERNAME_EXTRA),
					broadCastIntent);
		}

		broadCastIntent.setAction(action);
		this.sendBroadcast(broadCastIntent);

	}

	private void detailTwitter(String username, Intent intent) {

		try {
			twitter = TwitterFactory.getSingleton();
			User user = twitter.showUser(username);

			new TweetUserProcessor(this).parse(user,
					twitter.getUserTimeline(username));
			intent.putExtra(Clov3rConstant.TWITTER_NAME_EXTRA, user.getName());
			intent.putExtra(Clov3rConstant.TWITTER_USERNAME_EXTRA,
					user.getScreenName());

		} catch (TwitterException e) {
			e.printStackTrace();
		}

	}

	private void listTwitter() {
		twitter = tf.getInstance();
		Cursor cursor = getContentResolver()
				.query(EliGContentProvider.HASHTAG_CONTENT_URI, null, null,
						null, null);

		StringBuffer queryString = new StringBuffer();

		while (cursor.moveToNext()) {
			queryString.append(cursor.getString(cursor
					.getColumnIndex(EliGDatabase.HASHTAG_DESCRIPTION_COL)));
			if (!cursor.isLast())
				queryString.append(" OR ");
		}

		cursor.close();

		// Check limit for rows
		cursor = getContentResolver().query(
				EliGContentProvider.TWEET_CONTENT_URI, null, null, null,
				BaseColumns._ID + " ASC");
		if (cursor.getCount() > Clov3rConstant.LIMIT_ROWS) {

			cursor.moveToNext();
			int startId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
			int endId = startId
					+ (cursor.getCount() - Clov3rConstant.LIMIT_ROWS);
			getContentResolver().delete(EliGContentProvider.TWEET_CONTENT_URI,
					BaseColumns._ID + "< ?",
					new String[] { String.valueOf(endId) });

		}
		Long tweetId = null;
		if (cursor.moveToLast()) {// to move to the last tweet
			tweetId = cursor.getLong(cursor
					.getColumnIndex(EliGDatabase.TWEET_ID_COL));
		}

		cursor.close();
		Query query = new Query("#EleccionesNi OR #VotoNica OR #CSE #Nicaragua OR #Reeleccion");

		query.setLang("es");
		query.rpp(100);

		if (tweetId != null)
			query.setSinceId(tweetId);
		query.setResultType(Query.RECENT);

		QueryResult result;
		try {
			
			result = twitter.search(query);
			new TweetProcessor(this).parse(result.getTweets());
		} catch (TwitterException e) {
			e.printStackTrace();
		}

	}

}
