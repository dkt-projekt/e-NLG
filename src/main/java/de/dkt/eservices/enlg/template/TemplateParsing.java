package de.dkt.eservices.enlg.template;

import java.util.HashMap;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import de.dkt.eservices.enlg.linguistic.LinguisticFeature;
import de.dkt.eservices.enlg.linguistic.Sentence;

@Component
public class TemplateParsing {

	public Template JSON2Template(JSONObject json){
		Template t = new Template();
		t.objectName = json.getString("name");
		JSONObject jsonFeatures = json.getJSONObject("features");
		Set<String> keys = jsonFeatures.keySet();
		for (String key : keys) {
			t.features.put(key, jsonFeatures.getString(key));
		}
		return t;
	}

	public Template String2Template(String text){
		Template t = new Template();
		JSONObject json = new JSONObject(text);
		t.objectName = json.getString("name");
		JSONObject jsonSentences= json.getJSONObject("sentences");
		Set<String> keys = jsonSentences.keySet();
		for (String key : keys) {
			HashMap<Integer, LinguisticFeature> elements = new HashMap<Integer, LinguisticFeature>();
			JSONObject jsonFeatures= jsonSentences.getJSONObject(key);
			Set<String> keysFeatures = jsonFeatures.keySet();
			int counter=1;
			for (String keyFeature : keysFeatures) {
//				t.features.put(key, jsonFeatures.getString(keyFeature));
				
				//TODO GEnerate Linguistic Feature;
				LinguisticFeature lf = null;
				
				elements.put(counter, lf);
				counter++;
			}
			Sentence sent = new Sentence(elements);
			t.sentences.add(sent);
		}
		return t;
	}
}
