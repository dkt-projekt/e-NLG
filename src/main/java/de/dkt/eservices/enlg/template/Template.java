package de.dkt.eservices.enlg.template;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import de.dkt.eservices.enlg.linguistic.Sentence;

public class Template {

	public String objectName;
	
	public List<Sentence> sentences;
	public HashMap<String,String> features;
	
	/**
	 * A template has to include a set of sentences where every sentence includes its structure, and features, etc.
	 */
	
	public Template() {
		features = new HashMap<String, String>();
		sentences = new LinkedList<Sentence>();
	}
	
	public HashMap<String, String> getFeatures() {
		return features;
	}

	public String getObjectName() {
		return objectName;
	}

	public JSONObject getJSONObject(){
		JSONObject json = new JSONObject();
		json.put("name", objectName);
		Set<String> keys = features.keySet();
		JSONObject jsonFeatures = new JSONObject();
		for (String key : keys) {
			jsonFeatures.put(key, features.get(key));
		}
		json.put("features", jsonFeatures);
		return json;
	}
}
