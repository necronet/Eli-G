package net.clov3r.elig;

import net.clov3r.elig.http.HttpCaller;
import net.clov3r.elig.processor.ProcessorFactory;
import net.clov3r.elig.util.Clov3rConstant;

import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

public class LoaderService extends IntentService {

	private static final String LOG = LoaderService.class.getName();

	public LoaderService() {
		super("Configuration Loader Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		String url = getString(R.string.server)+ intent.getExtras().getString("url_suffix");
		HttpCaller caller = new HttpCaller();
		JSONObject object = caller.request(url);

		String action = intent.getExtras().getString("action");

		ProcessorFactory.createProcessor(getApplicationContext(), action)
				.parse(object);



		Intent broadCastIntent = new Intent();
		broadCastIntent.setAction(action);

		this.sendBroadcast(broadCastIntent);

	}

}
