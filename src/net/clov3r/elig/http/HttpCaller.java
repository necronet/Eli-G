package net.clov3r.elig.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpCaller {

	private static final String LOG = HttpCaller.class.getName();
	private static final String destination = null;

	public JSONObject request(String url) {
		JSONObject json = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		try {
			Log.d(LOG, url);
			httpPost.setHeader("Content-Type", "application/json");
			HttpResponse response = httpClient.execute(httpPost);

			Log.d(LOG, response.getStatusLine().toString());

			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// A Simple JSON Response Read
				InputStream instream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(instream));
				StringBuilder total = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					total.append(line);
				}
				Log.d(LOG, total.toString());
				json = new JSONObject(total.toString());
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	public String requestHtml(String url, List<NameValuePair> params) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		try {
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			UrlEncodedFormEntity query = new UrlEncodedFormEntity(params);
			httpPost.setEntity(query);

			HttpResponse response = httpClient.execute(httpPost);
			

			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// A Simple JSON Response Read
				InputStream instream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(instream));
				StringBuilder total = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					total.append(line);
				}
				Log.d(LOG, total.toString());
				return total.toString();
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
