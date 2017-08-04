package de.dkt.eservices.enlg.linguistic;

import org.json.JSONObject;

public class FeatureValue implements LinguisticFeature{

	public String name;
	public String value;
	
	public FeatureValue() {
		super();
	}
	
	public FeatureValue(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public JSONObject getJSONObject(){
		JSONObject json = new JSONObject();
		json.put("type", "feature_value");
		json.put("value", name);
		return json;
	}


}
