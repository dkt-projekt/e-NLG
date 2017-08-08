package de.dkt.eservices.enlg.realization;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import de.dkt.eservices.enlg.features.Feature;
import de.dkt.eservices.enlg.linguistic.Sentence;

@Component
public class SentenceExtractor {

	public HashMap<String, List<Sentence>> domainToSentences;
	
	public SentenceExtractor(){
		super();
	}
	
	@PostConstruct
	public void initializeModels(){
		domainToSentences = new HashMap<String,List<Sentence>>();
		
		//TODO load all the information about the sentences
		
	}
	
	public List<Sentence> extractSentences(String productType, String productName, List<Feature> features){
		List<Sentence> sentences = new LinkedList<Sentence>();

		//TODO
		
		
		
		
		return sentences;
	}

}
