package net.clov3r.elig;

import java.util.ArrayList;
import java.util.List;

import net.clov3r.elig.http.HttpCaller;
import net.clov3r.elig.processor.JrvProcessor;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.IntentService;
import android.content.Intent;

public class CSEService extends IntentService {

	private static final String LOG = CSEService.class.getName();
	private static final String searchUrl="buscarjrv.php";

	public CSEService() {
		super("Configuration Loader Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		String url = getString(R.string.cseserver)+ searchUrl;
		HttpCaller caller = new HttpCaller();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("cedula", intent.getExtras().getString("id")));
		params.add(new BasicNameValuePair("tipo", "C"));
		params.add(new BasicNameValuePair("buscar", "Buscar"));
		
		String object = caller.requestHtml(url,params);
		
		Document doc=Jsoup.parse(object);
		Elements datas = doc.select("b");
		
		String action = intent.getExtras().getString("action");
		Intent broadCastIntent = new Intent();
		broadCastIntent.setAction(action);
		
		if(datas.size()>0){
			new JrvProcessor(this).parse(object);
			broadCastIntent.putExtra("success", true);
			broadCastIntent.putExtra("id",intent.getExtras().getString("id") );
		}
		this.sendBroadcast(broadCastIntent);

	}

}
