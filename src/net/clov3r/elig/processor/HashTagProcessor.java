package net.clov3r.elig.processor;

import net.clov3r.elig.ServiceHelper;
import net.clov3r.elig.database.EliGContentProvider;
import net.clov3r.elig.database.EliGDatabase;
import net.clov3r.elig.util.Clov3rConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

public class HashTagProcessor implements Processor {

	private final static String LOG = HashTagProcessor.class.getName();
	
	private Context context;
	
	public HashTagProcessor(Context context){
		this.context=context;
		
	}
	
	@Override
	public void parse(JSONObject object) {
		JSONArray hashtagsJSON;
		try {
			hashtagsJSON = object.getJSONArray("rows");
			ContentResolver cr=context.getContentResolver();
			cr.delete(EliGContentProvider.HASHTAG_CONTENT_URI, null, null);
			ContentValues[] contentValues=new ContentValues[hashtagsJSON.length()];
			for (int i = 0; i < hashtagsJSON.length(); i++) {
				ContentValues cv=new ContentValues();
				cv.put(EliGDatabase.HASHTAG_DESCRIPTION_COL,hashtagsJSON.getString(i));
				contentValues[i]=cv;
				cr.insert(EliGContentProvider.HASHTAG_CONTENT_URI, cv);
			}
			
			ServiceHelper.getInstance(context).startService(EliGActions.TWITTER_LIST_ACTION);
			
			SharedPreferences settings = context.getSharedPreferences(Clov3rConstant.PREFERENCES, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(Clov3rConstant.LOADED_HASHTAG_PREF, true);
			// Commit the edits!
			editor.commit();
			
		} catch (JSONException e) {
			e.printStackTrace();
			// Notify error
		}

	}

}
