package net.clov3r.elig.processor;
import static net.clov3r.elig.processor.EliGActions.*;
import android.content.Context;

public class ProcessorFactory {

	public static Processor createProcessor(Context context,String action){
		
		if(action.equals(TWITTER_HASHTAG_ACTION))
			return new HashTagProcessor(context);
		
		
		return null;
	}
	
}
