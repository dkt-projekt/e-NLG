package de.dkt.eservices.enlg.linguistic;

import org.json.JSONObject;

public class VerbFeature implements LinguisticFeature {

	public String name;

	public VerbFeature() {
		super();
	}
	
	public VerbFeature(String name) {
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
		json.put("type", "verb");
		json.put("value", name);
		return json;
	}


}
