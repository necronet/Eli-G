package net.clov3r.elig;

import net.clov3r.elig.fragment.TwitterUserDetailFragment;
import net.clov3r.elig.processor.EliGActions;
import net.clov3r.elig.util.Clov3rConstant;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;

public class TweetDetailActivity extends FragmentActivity {

	private BroadcastReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tweet_user_detail_fragment);
	}

	@Override
	protected void onStart() {
		super.onStart();
		ActionBar actionBar = this.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}

		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		receiver = new EliGBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(EliGActions.TWITTER_DETAIL_LIST_ACTION);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.home, menu);

		return super.onCreateOptionsMenu(menu);
	}

	class EliGBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					EliGActions.TWITTER_DETAIL_LIST_ACTION)) {
				TwitterUserDetailFragment twitterListFragment = (TwitterUserDetailFragment) getSupportFragmentManager()
						.findFragmentById(R.id.tweetDetail);
				if (twitterListFragment != null) {
					twitterListFragment
							.refresh(
									intent.getStringExtra(Clov3rConstant.TWITTER_USERNAME_EXTRA),
									intent.getStringExtra(Clov3rConstant.TWITTER_NAME_EXTRA));
				}
				// setProgressBarIndeterminateVisibility(Boolean.FALSE);
				// menuRefresh.setVisible(true);
			}
		}

	}

}
