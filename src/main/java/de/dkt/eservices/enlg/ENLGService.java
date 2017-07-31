package de.dkt.eservices.enlg;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.dkt.eservices.enlg.openccg.OpenCCG;
import de.dkt.eservices.enlg.realization.TextRealizer;
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
		resultText = realizer.generateJSONTextFromTemplate(inputTemplate, features);
		return resultText;
	}

	public String generateStringTextFromTemplate(String templateText, HashMap<String, String> features) {
		String resultText = null;
		Template inputTemplate = templateParsing.String2Template(templateText);
		resultText = realizer.generateStringTextFromTemplate(inputTemplate, features);
		return resultText;
	}

	public String generateGrammar(String input, String domain) {
		try{
			return openccg.generateGrammar(input, domain);
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public String generateTextFromGrammar(String input, String domain) {
		try{
			return openccg.generateTextFromGrammar(input, domain);
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
