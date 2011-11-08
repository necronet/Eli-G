package net.clov3r.elig.processor;

import org.json.JSONObject;

public interface Processor {

	public void parse(JSONObject object);
	
}
