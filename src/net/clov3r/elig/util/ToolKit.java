package net.clov3r.elig.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Class that define several method that are globally use on the application and
 * have no bussiness related or direct relation with any class. Example
 * 
 * - Create the service URL from a Context - Check wether Network connection is
 * available
 * 
 * */

public class ToolKit {

	private static String LOG = ToolKit.class.getName();

	/**
	 * Checks wether there is an internet connection available
	 * 
	 * */
	public static boolean isInternetAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null)
			return cm.getActiveNetworkInfo().isConnectedOrConnecting();
		else
			return false;
	}

	/**
	 * Get a friendly string about the time passed until now. For example return
	 * 
	 * 1 hour ago 45 min ago 1 min ago
	 * @throws ParseException 
	 * 
	 * */
	public static String getFriendlyTime(Long time) throws ParseException {
		Calendar today = Calendar.getInstance();
		DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-6"));
		Date expiry = new Date(time);
		String realDateString=dateFormat.format(expiry);
		
		long timeDiff = (today.getTimeInMillis() - new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(realDateString).getTime()) / 1000;
		long topSeconds = 60;
		long topMin = topSeconds * 60;
		long topHours = topMin * 24;
		long topDays = topHours * 4;

		if (timeDiff < topSeconds) {
			
			return "Hace pocos segs";
		} else if (timeDiff >= 60 && timeDiff < topMin) {
			return "Hace " + (int) timeDiff / topSeconds + " min";
		} else if (timeDiff >= topMin && timeDiff < topHours) {
			int hours = (int) (timeDiff / topMin);
			if (hours >= 2)
				return "Hace " + hours + " horas";
			else
				return "Hace " + hours + " hora";
		} else if (timeDiff >= topHours && timeDiff < topDays){
			int days = (int)(timeDiff / topHours);
			if(days>=2)
				return "Hace "+days + " d\u00EDas";
			else
				return "Hace "+days + " d\u00EDa";
		}else{
			
			return realDateString;
		}
	}
}
