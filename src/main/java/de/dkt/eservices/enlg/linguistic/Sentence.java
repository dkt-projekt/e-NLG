package de.dkt.eservices.enlg.linguistic;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.json.JSONObject;

import edu.stanford.nlp.pipeline.Annotation;

public class Sentence {

	/**
	 * Put it every element of the sentence the type of generator that it needs to be for the generation process.
	 */
	Annotation annotation;
	
	public HashMap<Integer, LinguisticFeature> elements;

	public List<LinguisticFeature> features;

	public Sentence() {
		super();
		elements = new HashMap<Integer, LinguisticFeature>();
	}

	public Sentence(HashMap<Integer, LinguisticFeature> elements) {
		super();
		this.elements = elements;
	}

	public JSONObject getJSONObject(){
		JSONObject json = new JSONObject();
		JSONArray featuresArray = new JSONArray();
		for (LinguisticFeature s : features) {
			featuresArray.put(s.getJSONObject());
		}
		json.put("features", featuresArray);
		return json;
	}
	
}
