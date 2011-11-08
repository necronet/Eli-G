package net.clov3r.elig;

import static net.clov3r.elig.processor.EliGActions.TWITTER_DETAIL_LIST_ACTION;
import static net.clov3r.elig.processor.EliGActions.TWITTER_HASHTAG_ACTION;
import static net.clov3r.elig.processor.EliGActions.TWITTER_LIST_ACTION;
import net.clov3r.elig.processor.EliGActions;
import net.clov3r.elig.util.Clov3rConstant;
import net.clov3r.elig.util.ToolKit;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ServiceHelper {

	private static final String LOG = ServiceHelper.class.getName();
	private Context context;
	private static ServiceHelper serviceHelper;

	public static ServiceHelper getInstance(Context context) {
		if (serviceHelper == null)
			serviceHelper = new ServiceHelper(context);
		// to update the context each time a service helper it's called
		serviceHelper.setContext(context);
		return serviceHelper;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public ServiceHelper(Context context) {
		this.context = context;
	}

	public boolean startService(String action) {
		return startService(action, null);
	}

	public boolean startService(String action, Bundle extras) {
		if (ToolKit.isInternetAvailable(context)) {
			Intent intent = null;
			if (action.equals(TWITTER_HASHTAG_ACTION)) {
				intent = new Intent(context, LoaderService.class);
				intent.putExtra("action", action);
				intent.putExtra("url_suffix", Clov3rConstant.TWITTER_URL);
			} else if (action.equals(TWITTER_LIST_ACTION)
					|| action.equals(TWITTER_DETAIL_LIST_ACTION)) {
				intent = new Intent(context, TwitterService.class);
				intent.putExtra("action", action);
				if(extras!=null)
					intent.putExtras(extras);
			}else if(action.equals(EliGActions.SEARCH_ID_ACTION)){
				intent = new Intent(context, CSEService.class);
				intent.putExtra("action", action);
				intent.putExtras(extras);
				
			}
			
			context.startService(intent);
			return true;
		} else {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.toast_message, null);

			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setText(R.string.no_internet_available);

			Toast toast = new Toast(context);
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setView(layout);
			toast.show();

			toast.show();
			return false;
		}
	}

}
