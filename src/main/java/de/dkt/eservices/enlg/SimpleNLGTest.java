package de.dkt.eservices.enlg;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.dkt.eservices.enlg.realization.TextRealizer;
import de.dkt.eservices.enlg.template.Template;
import de.dkt.eservices.enlg.template.TemplateGenerator;
import simplenlg.aggregation.AggregationRule;
import simplenlg.aggregation.BackwardConjunctionReductionRule;
import simplenlg.aggregation.ForwardConjunctionReductionRule;
import simplenlg.features.Feature;
import simplenlg.features.InterrogativeType;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseCategory;
import simplenlg.lexicon.Lexicon;
import simplenlg.morphology.english.DeterminerAgrHelper;
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

		TemplateGenerator tg = new TemplateGenerator();
		tg.initializeModels();
		List<String> texts = new LinkedList<String>();
		texts.add("This Samsung S8 mobile phone has a great screen and an awesome battery");
		texts.add("My new skirt is red and its shape is fitting me perfectly");
		tg.generateTemplate(texts);
		System.exit(0);
		
		TextRealizer ng = new TextRealizer();
		Template t = new Template();
		t.objectName = "Samsung S8";
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("contain", "3GB RAM memory");
		hm.put("offer", "6 inch screen");
		hm.put("load", "30 minutes");
		//hm.put("have", "20 Mpx camera");
		hm.put("have", "elephant");
		// TODO: try something with same verb...
		t.features=hm;
		
//		String text = ng.generateTextFromTemplate(t);
//		System.out.println("plain:\t\t" + text);
		String text2 = ng.generateCoordinateTextFromTemplate(t);
		System.out.println("simple coord:\t" + text2);

		Lexicon lexicon = Lexicon.getDefaultLexicon();
		Realiser realiser = new Realiser(lexicon);
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		// TODO: check out grammatical framework.org as text generation tool
		// TODO: check out rdf2text
		ForwardConjunctionReductionRule fcr = new ForwardConjunctionReductionRule();
		BackwardConjunctionReductionRule bcr = new BackwardConjunctionReductionRule();
		
		CoordinatedPhraseElement c1 = nlgFactory.createCoordinatedPhrase();
		DeterminerAgrHelper dah = new DeterminerAgrHelper();
		for (String key : hm.keySet()){
			SPhraseSpec s = null;
			if (dah.requiresAn(hm.get(key))){
				s = nlgFactory.createClause(t.getObjectName(), key, "an " + hm.get(key));
			}
			else{
				s = nlgFactory.createClause(t.getObjectName(), key, "a " + hm.get(key));
			}
			c1.addCoordinate(s);
		}
		fcr.apply(c1);
		System.out.println("fcr:\t\t" + realiser.realiseSentence(c1));
		bcr.apply(c1);
		System.out.println("bcr:\t\t" + realiser.realiseSentence(c1));
		
		
	}

}
