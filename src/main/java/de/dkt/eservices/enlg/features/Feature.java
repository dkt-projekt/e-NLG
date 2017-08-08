package de.dkt.eservices.enlg.features;

import java.util.LinkedList;
import java.util.List;

public class Feature {

	public String featureId;
	
	public String name;
	public List<String> values;
	public String currentValue;
	
	public Feature() {
		super();
		values = new LinkedList<String>();
	}
	
	public Feature(String id,String name, List<String> values, String currentValue) {
		super();
		this.featureId=id;
		this.name = name;
		this.values = values;
		this.currentValue = currentValue;
	}
	
	public void addValue(String value){
		values.add(value);
	}
}
