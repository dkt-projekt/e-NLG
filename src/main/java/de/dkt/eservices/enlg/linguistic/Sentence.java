package de.dkt.eservices.enlg.linguistic;

import java.util.HashMap;

public class Sentence {

	/**
	 * Put it every element of the sentence the type of generator that it needs to be for the generation process.
	 */
	
	
	public HashMap<Integer, LinguisticFeature> elements;

	public Sentence(HashMap<Integer, LinguisticFeature> elements) {
		super();
		this.elements = elements;
	}

	
}
