package de.dkt.eservices.enlg.realization;

import org.springframework.stereotype.Component;

@Component
public class TextRealizer {

//	Lexicon lexicon = null;
//	NLGFactory nlgFactory = null;
//	Realiser realiser = null;
//
//	public TextRealizer(){
//		lexicon = Lexicon.getDefaultLexicon();
//		nlgFactory = new NLGFactory(lexicon);
//		realiser = new Realiser(lexicon);
//	}
//
//	public String generateStringTextFromTemplate(Template template, HashMap<String, String> features){
//		String result = "";
//		String objectName = template.getObjectName();
//		//Generate a sentence with every feature.
//		//HashMap<String,String> features = template.getFeatures();
//		Set<String> keys = features.keySet();
//		for (String k : keys) {
//			String newSentence = "";
//		    SPhraseSpec p = nlgFactory.createClause();
//		    p.setSubject(objectName);
//		    p.setVerb(k);
//		    p.setObject(features.get(k));
//		    newSentence = realiser.realiseSentence(p);
//			result = result + "" + newSentence + " ";
//		}
//		return result;
//	}
//
//	public JSONObject generateJSONTextFromTemplate(Template template, HashMap<String, String> features){
//		JSONObject resultJSON = new JSONObject();
//		String objectName = template.getObjectName();
//		//Generate a sentence with every event.
//		//HashMap<String,String> features = template.getFeatures();
//		int counter = 1;
//		for (Sentence sentence : template.sentences) {
//			String sent = realizeSentenceTextFromFeatures(sentence, features);
//			resultJSON.put("sentence"+counter, sent);
//			counter++;
//		}
////		Set<String> keys = features.keySet();
////		int counter=1;
////		for (String k : keys) {
////			String newSentence = "";
////		    SPhraseSpec p = nlgFactory.createClause();
////		    p.setSubject(objectName);
////		    p.setVerb(k);
////		    p.setObject(features.get(k));
////		    newSentence = realiser.realiseSentence(p);
////			resultJSON.put("option"+counter, newSentence);
////			counter++;
////		}
//		return resultJSON;
//	}
//
//	public String realizeSentenceTextFromFeatures(Sentence sentence, HashMap<String, String> features){
//		String result = "";
//		Set<Integer> keys = sentence.elements.keySet();
//		for (Integer key : keys) {
//			LinguisticFeature lf = sentence.elements.get(key);
//			if(lf instanceof ProductType){
//				
//				
//				
//			}
//			else{
//				
//			}
//		}
//		return result;
//	}
//
//	public String generateCoordinateTextFromTemplate(Template template){
//		String objectName = template.getObjectName();
//		HashMap<String,String> features = template.getFeatures();
//		Set<String> keys = features.keySet();
//	    CoordinatedPhraseElement c = nlgFactory.createCoordinatedPhrase();
//		for (String k : keys) {
//		    SPhraseSpec s1 = nlgFactory.createClause(objectName, k, features.get(k));
//		    c.addCoordinate(s1);
//		}
//		return realiser.realiseSentence(c);
//	}

}
