package net.clov3r.elig;

import static net.clov3r.elig.processor.EliGActions.TWITTER_HASHTAG_ACTION;
import net.clov3r.elig.database.EliGDatabase;
import net.clov3r.elig.fragment.BarChoiceFragment;
import net.clov3r.elig.fragment.SearchIdFragment;
import net.clov3r.elig.fragment.TwitterListFragment;
import net.clov3r.elig.processor.EliGActions;
import net.clov3r.elig.util.Clov3rConstant;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.Window;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * First called activity prepared the scenario for the application to work main
 * task is:
 * 
 * - Load the twitter hashtag into memory. - Rendered the Fragment to displayed
 * the tabbar on top of the app
 */

public class HomeActivity extends FragmentActivity {

	private static final String LOG = HomeActivity.class.getName();
	private MenuItem menuRefresh;
	private ServiceHelper serviceHelper;
	private BroadcastReceiver receiver;
	private SharedPreferences preferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.home);

		preferences = getSharedPreferences(Clov3rConstant.PREFERENCES, 0);
		if (!preferences.getBoolean(Clov3rConstant.LOADED_HASHTAG_PREF, false)) {
			serviceHelper = ServiceHelper.getInstance(this.getApplicationContext());
			serviceHelper.startService(TWITTER_HASHTAG_ACTION);
			
		}
		if (!preferences.getBoolean("alarm_setup", false)) 
			setupAlarm();
	}
	
	private void setupAlarm() {

		Intent i=new Intent(this, TwitterService.class);
		PendingIntent pi=PendingIntent.getService(this, 0, i, 0);
		i.putExtra("action", EliGActions.TWITTER_LIST_ACTION);
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME,SystemClock.elapsedRealtime(), 60*30*1000, pi);
		
		SharedPreferences settings = getSharedPreferences(Clov3rConstant.PREFERENCES, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(Clov3rConstant.ALARM_SETUP_PREF,true);
		// Commit the edits!
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.home, menu);
		menuRefresh = menu.findItem(R.id.mnuRefresh);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		long id = item.getItemId();

		if (id == R.id.mnuRefresh) {
			BarChoiceFragment barChoice = (BarChoiceFragment) getSupportFragmentManager()
					.findFragmentById(R.id.choiceFragment);
			if (barChoice.refresh()) {
				item.setVisible(false);
				setProgressBarIndeterminateVisibility(Boolean.TRUE);
			}
		} else if (id == android.R.id.home) {
			showDialog();

		}
		return super.onOptionsItemSelected(item);
	}

	public void showDialog() {

		// DialogFragment.show() will take care of adding the fragment
		// in a transaction. We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment prev = getSupportFragmentManager().findFragmentByTag("about");
		if (prev != null) {
			ft.remove(prev);
		}

		ft.addToBackStack(null);

		// Create and show the dialog.
		DialogFragment newFragment = AboutDialogFragment.newInstance();
		newFragment.show(ft, "about");

	}

	public static class AboutDialogFragment extends DialogFragment {

		static AboutDialogFragment newInstance() {
			AboutDialogFragment f = new AboutDialogFragment();
			f.setCancelable(true);
			f.setStyle(STYLE_NO_TITLE, 0);
			return f;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.about, container, false);

			// setTitle(getString(R.string.app_title));
			WebView aboutView = (WebView) v.findViewById(R.id.webViewAbout);

			// About text
			String text = "<html><body style='p {text-align:justify;}' >"
					+ "<p>"
					+ "Eli-G Elecciones 2011, Nicaragua"
					+ "</p>"
					+ "<p style='text-align:justify'>Eli-G es una aplicaci&oacute;n gratuita para facilitar la informaci&oacute;n durante las elecciones 2011 en Nicaragua, por medio de redes sociales, noticias y actualizaci&oacute;n"
					+ " de informaci&oacute;n en tiempo real."
					+ " .<br/><br/>"
					+ "Los enlaces a otros sitios web que no sean propiedad de Eli-G o el grupo Clov3r, son ofrecidos como un servicio gratuito y no nos hacemos responsable de su contenido."
					+ " Si tiene alguna pregunta, comentario &oacute; sugerencia acerca de esta aplicacion, por favor, escribanos a <a href=\"mailto:android@clov3r.net\" >android@clov3r.net</a> &oacute; <a href=\"mailto:joseayerdis@gmail.com\" >joseayerdis@gmail.com</a>"
					+ "</p></body></html>";

			aboutView.loadData(text, "text/html", "utf-8");
			return v;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		receiver = new EliGBroadcast();

		IntentFilter filter = new IntentFilter();

		filter.addAction(EliGActions.TWITTER_LIST_ACTION);
		filter.addAction(EliGActions.SEARCH_ID_ACTION);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(receiver);

	}

	class EliGBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(EliGActions.TWITTER_LIST_ACTION)) {
				TwitterListFragment twitterListFragment = (TwitterListFragment) getSupportFragmentManager()
						.findFragmentByTag("twitterList");
				if (twitterListFragment != null)
					twitterListFragment.refresh();

				setProgressBarIndeterminateVisibility(Boolean.FALSE);
				menuRefresh.setVisible(true);
			}else if (intent.getAction().equals(EliGActions.SEARCH_ID_ACTION) && intent.getBooleanExtra("success",false)) 
			{
				SearchIdFragment searchIdFragment = (SearchIdFragment) getSupportFragmentManager()
						.findFragmentByTag("searchId");
				
				if (searchIdFragment != null)
					searchIdFragment.refresh(intent.getStringExtra("id"));

				setProgressBarIndeterminateVisibility(Boolean.FALSE);
				
			}
		}

	}

}