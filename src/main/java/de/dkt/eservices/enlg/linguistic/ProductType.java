package de.dkt.eservices.enlg.linguistic;

import org.json.JSONObject;

public class ProductType implements LinguisticFeature{

	public String name;
	
	public ProductType() {
		super();
	}
	
	public ProductType(String name) {
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
		json.put("type", "product_type");
		json.put("value", name);
		return json;
	}


}
