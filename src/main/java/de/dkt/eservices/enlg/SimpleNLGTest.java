package de.dkt.eservices.enlg;

import java.util.HashMap;

import simplenlg.features.Feature;
import simplenlg.features.InterrogativeType;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class SimpleNLGTest {

	public static void main(String[] args) {
//		Lexicon lexicon = Lexicon.getDefaultLexicon();
//		NLGFactory nlgFactory = new NLGFactory(lexicon);
//		Realiser realiser = new Realiser(lexicon);
//
//		NLGElement sent1 = nlgFactory.createSentence("my dog is happy");
//
//		String output = realiser.realiseSentence(sent1);
//		System.out.println(output);
//
//		
//	    SPhraseSpec p = nlgFactory.createClause();
//	    p.setSubject("Mary");
//	    p.setVerb("chase");
//	    p.setObject("the monkey");
//	    
//	    p.setFeature(Feature.TENSE, Tense.PAST);
//	    p.setFeature(Feature.NEGATED, true);
//	    
//	    System.out.println(realiser.realiseSentence(p));
//	    
//	    p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
//	    System.out.println(realiser.realiseSentence(p));
//	    
//	    p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_OBJECT);
////	    p.addComplement("very quickly"); // Adverb phrase, passed as a string
////	    p.addComplement("despite her exhaustion"); // Prepositional phrase, string
//	    System.out.println(realiser.realiseSentence(p));
//	    
//	    SPhraseSpec s1 = nlgFactory.createClause("my cat", "like", "fish");
//	    SPhraseSpec s2 = nlgFactory.createClause("my dog", "like", "big bones");
//	    SPhraseSpec s3 = nlgFactory.createClause("the football players", "like", "grass");
//
//	    CoordinatedPhraseElement c = nlgFactory.createCoordinatedPhrase();
//	    c.addCoordinate(s1);
//	    c.addCoordinate(s2);
//	    c.addCoordinate(s3);
//	    
//
//	    System.out.println( realiser.realiseSentence(c));

		NLPGeneration ng = new NLPGeneration();
		
		Template t = new Template();
		t.objectName = "Samsung S8";
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("contain", "3GB RAM memory");
		hm.put("offer", "6 inch screen");
		hm.put("load", "30 minutes");
		hm.put("have", "20 Mpx camera");
		t.features=hm;
		
		String text = ng.generateTextFromTemplate(t);
		System.out.println(text);
		String text2 = ng.generateCoordinateTextFromTemplate(t);
		System.out.println(text2);
		
	}

}
