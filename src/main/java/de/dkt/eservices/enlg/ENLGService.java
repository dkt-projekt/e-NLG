package de.dkt.eservices.enlg;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.dkt.eservices.enlg.features.Feature;
import de.dkt.eservices.enlg.features.FeatureExtractor;
import de.dkt.eservices.enlg.linguistic.Sentence;
import de.dkt.eservices.enlg.openccg.OpenCCG;
import de.dkt.eservices.enlg.realization.SentenceExtractor;
import de.dkt.eservices.enlg.realization.TextRealizer;
import de.dkt.eservices.enlg.realization.TextRealizerFromExample;
import de.dkt.eservices.enlg.template.Template;
import de.dkt.eservices.enlg.template.TemplateGenerator;
import de.dkt.eservices.enlg.template.TemplateParsing;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 * The whole documentation about WEKA examples can be found in ...
 *
 */
@Component
public class ENLGService {
    
	Logger logger = Logger.getLogger(ENLGService.class);

	@Autowired
	TemplateParsing templateParsing;

	@Autowired
	TextRealizer realizer;
	
	@Autowired
	TemplateGenerator generator;
	
	@Autowired
	OpenCCG openccg;
	
	@Autowired
	FeatureExtractor featureExtractor;
	
	@Autowired
	TextRealizerFromExample textRealizer;
	
	@PostConstruct
	public void initializeModels(){
		featureExtractor = new FeatureExtractor();
		featureExtractor.initializeModels();
		textRealizer = new TextRealizerFromExample();
		textRealizer.initializeModels();
	}
	
	public static void main(String[] args) {
		ENLGService service = new ENLGService();
		service.initializeModels();
		String output = service.generateDescription("tv", "karrimor s2", "sleep, 43 in, wireless", "");
		System.out.println("OUTPUT: "+output);
	}
	
	public String generateDescription (String type, String name, String sFeatures, String language){
		List<Feature> features = featureExtractor.extractFeaturesFromString(type, sFeatures);
		
//		System.out.println("Obtained features:");
//		for (Feature feature : features) {
//			if(feature.values==null){
//				System.out.println("\t"+feature.name + "--NULL--"+feature.currentValue);
//			}
//			else{
//				System.out.println("\t"+feature.name + "--"+feature.values.toString()+"--"+feature.currentValue);
//			}
//		}
		String result = textRealizer.realizeText(type, name, features);
//		System.out.println("FINAL text: \""+result+"\"");
		return result;
	}
	
    public JSONObject generateTemplate(String inputText, String algorithm, String language) throws ExternalServiceFailedException, BadRequestException {
    	try {
    		List<String> texts = new LinkedList<String>();
    		JSONObject json = new JSONObject(inputText);
    		JSONArray array = json.getJSONArray("texts");
    		for (int i = 0; i < array.length(); i++) {
    			texts.add(array.getString(i));
			}
    		Template resultTemplate = generator.generateTemplate(texts);
    		return resultTemplate.getJSONObject();
    	} catch (Exception e) {
    		e.printStackTrace();
			logger.error("EXCEPTION: "+e.getMessage());
    		throw new ExternalServiceFailedException(e.getMessage());
    	}
    }

	public JSONObject generateTextFromTemplate(String templateText, String sFeatures) {
		JSONObject resultText = null;
		Template inputTemplate = templateParsing.String2Template(templateText);
		HashMap<String,String> features = new HashMap<String, String>();
		JSONObject json = new JSONObject(sFeatures);
		Set<String> keys = json.keySet();
		for (String key : keys) {
			features.put(key, json.getString(key));
		}
		resultText = null;//realizer.generateJSONTextFromTemplate(inputTemplate, features);
		return resultText;
	}

	public String generateStringTextFromTemplate(String templateText, HashMap<String, String> features) {
		String resultText = null;
		Template inputTemplate = templateParsing.String2Template(templateText);
		resultText = null;//realizer.generateStringTextFromTemplate(inputTemplate, features);
		return resultText;
	}

	public String generateGrammar(String input, String domain) {
		try{
			return null;//openccg.generateGrammar(input, domain);
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public String generateTextFromGrammar(String input, String domain) {
		try{
			return null;//openccg.generateTextFromGrammar(input, domain);
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
