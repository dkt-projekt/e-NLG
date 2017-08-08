package de.dkt.eservices.enlg.linguistic;

import org.json.JSONObject;

public class ProductName implements LinguisticFeature {

	public String name;

	public ProductName() {
		super();
	}
	
	public ProductName(String name) {
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
		json.put("type", "product_name");
		json.put("value", name);
		return json;
	}


}
