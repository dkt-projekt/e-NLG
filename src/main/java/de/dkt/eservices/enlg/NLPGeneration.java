package de.dkt.eservices.enlg;

import java.util.HashMap;
import java.util.Set;

import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class NLPGeneration {

	Lexicon lexicon = null;
	NLGFactory nlgFactory = null;
	Realiser realiser = null;

	public NLPGeneration(){
		lexicon = Lexicon.getDefaultLexicon();
		nlgFactory = new NLGFactory(lexicon);
		realiser = new Realiser(lexicon);
	}

	public String generateTextFromTemplate(Template template){
		String result = "";
		
		String objectName = template.getObjectName();
		
		//Generate a sentence with every event.
		HashMap<String,String> features = template.getFeatures();
		Set<String> keys = features.keySet();
		for (String k : keys) {
			
			String newSentence = "";
		    SPhraseSpec p = nlgFactory.createClause();
		    p.setSubject(objectName);
		    p.setVerb(k);
		    p.setObject(features.get(k));
		    newSentence = realiser.realiseSentence(p);
			
			result = result + "" + newSentence + " ";
		}
		return result;
	}
	
	public String generateCoordinateTextFromTemplate(Template template){
		String objectName = template.getObjectName();
		HashMap<String,String> features = template.getFeatures();
		Set<String> keys = features.keySet();
	    CoordinatedPhraseElement c = nlgFactory.createCoordinatedPhrase();
		for (String k : keys) {			
		    SPhraseSpec s1 = nlgFactory.createClause(objectName, k, features.get(k));
		    c.addCoordinate(s1);
		}
		return realiser.realiseSentence(c);
	}
	

}
