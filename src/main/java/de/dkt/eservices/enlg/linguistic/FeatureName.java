package de.dkt.eservices.enlg.linguistic;

import org.codehaus.jettison.json.JSONArray;
import org.json.JSONObject;

public class FeatureName implements LinguisticFeature{

	public String name;
	
	public FeatureName() {
		super();
	}
	
	public FeatureName(String name, String value) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public JSONObject getJSONObject(){
		JSONObject json = new JSONObject();
		json.put("type", "feature_name");
		json.put("value", name);
		return json;
	}

}
