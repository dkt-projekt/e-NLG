package de.dkt.eservices.enlg.realization;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.cypher.internal.compiler.v2_1.ast.Foreach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import de.dkt.eservices.enlg.features.Feature;
import de.dkt.eservices.enlg.features.FeatureExtractor;
import de.dkt.eservices.enlg.linguistic.Sentence;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

@Component
public class TextRealizerFromExample {

	public HashMap<String, HashMap<String,HashMap<String,Integer>>> domainToFeatureToSentences;
	public HashMap<String, HashMap<String, List<String>>> domainToFeatureValuesByName;
	public HashMap<String, HashMap<String,Integer>> domainToBLABLA;
	public HashMap<String, HashMap<String,Integer>> domainToProductNameSentences;
	
	public String folderPath = "jsonFiles/";
	
	private StanfordCoreNLP pipeline;
	
	private FeatureExtractor featureExtractor; 

	HashMap<String, List<String>> featureValuesByName = new HashMap<String, List<String>>();

	public TextRealizerFromExample() {
		super();
	}

	@PostConstruct
	public void initializeModels(){
		try{
			featureExtractor = new FeatureExtractor();
			featureExtractor.initializeModels();

			Properties props;
			props = new Properties();
//			props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, depparse");
			props.put("annotators", "tokenize, ssplit");
			this.pipeline = new StanfordCoreNLP(props);

			//		domainToFeatureToSentences = new HashMap<String, HashMap<Feature,List<Sentence>>>();
			domainToFeatureValuesByName = new HashMap<String, HashMap<String,List<String>>>();
			domainToBLABLA = new HashMap<String, HashMap<String,Integer>>();
			domainToProductNameSentences = new HashMap<String, HashMap<String,Integer>>();
			domainToFeatureToSentences = new HashMap<String, HashMap<String,HashMap<String,Integer>>>();

			String types[]= {"headphone","tv","phone"};
//			String types[]= {"headphone"};
			for (String type : types) {
//				System.out.println("Starting loading sentences from type: "+type);
				HashMap<String, List<String>> featureValuesByName = new HashMap<String, List<String>>();
				String jsonText ="";
				try{
					ClassPathResource cpr = new ClassPathResource(folderPath + type + ".json");
					jsonText = IOUtils.toString(new FileInputStream(cpr.getFile()));
					//TODO If we want to do more sophisticated NLP then that could be a problem
					jsonText = jsonText.toLowerCase();
				}
				catch(Exception e){
					System.out.println("ERROR: at loading the jsonfile for: "+type);
				}
				JSONArray array = new JSONArray(jsonText);
//				System.out.print("Filling the features...");
//				for (int i = 0; i < array.length(); i++) {
//					JSONObject json = (JSONObject)array.get(i);
//					String desc = "";
//					Set<String> keys = json.keySet();
//					String product_name="",product_type="";
//					for (String key : keys) {
//						if(key.equalsIgnoreCase("product_name") ){
//							product_name = json.getString(key);
//						}
//						else if(key.equalsIgnoreCase("product_type") ){
//							product_type= json.getString(key);
//						}
//						else if(key.equalsIgnoreCase("description") ){
//							desc = json.getString(key);
//						}
//						else if(!key.equalsIgnoreCase("id") && !key.equalsIgnoreCase("product_gtin") && !key.equalsIgnoreCase("urls") ){
//							if(!featureValuesByName.containsKey(key)){
//								featureValuesByName.put(key, new LinkedList<String>());
//							}
//							String value = json.get(key).toString();
//							if(!value.equalsIgnoreCase("")){
//								if(!featureValuesByName.get(key).contains(value)){
//									featureValuesByName.get(key).add(value);
//								}
//							}
//						}
//					}
//				}
				List<Feature> listFeatures = featureExtractor.domainToFeatures.get(type);
//				
////				System.out.println("DONE");
//				domainToFeatureValuesByName.put(type, featureValuesByName);

//				HashMap<Feature,List<Sentence>> features2Sentences = new HashMap<Feature,List<Sentence>>();

				HashMap<String,Integer> blablaSentences = new HashMap<String, Integer>();
				HashMap<String,Integer> productNameSentences = new HashMap<String, Integer>();
				HashMap<String, HashMap<String,Integer>> fnToSentenceWithFrequency = new HashMap<String, HashMap<String,Integer>>();

				for (int i = 0; i < array.length(); i++) {
//					System.out.println("Processing "+(i+1)+" from "+array.length());
					JSONObject json = (JSONObject)array.get(i);
					String desc = json.getString("description");
					if(!desc.equalsIgnoreCase("")){
						Set<String> keys = json.keySet();
						String product_name="",product_type="";
						for (String key : keys) {
							if(key.equalsIgnoreCase("product_name") ){
								product_name = json.getString(key);
							}
							else if(key.equalsIgnoreCase("product_type") ){
								product_type= json.getString(key);
							}
						}									

//						Set<String> featureNames = featureValuesByName.keySet();

//						System.out.println("Replacing features.");
//						String desc2 = replaceFeatures(desc,product_name,product_type,featureValuesByName);
						
//						System.out.println("Starting Annotation...");
						Annotation annotation = new Annotation(desc);
						pipeline.annotate(annotation);
//						Annotation annotation2 = new Annotation(desc2);
//						pipeline.annotate(annotation2);

//						System.out.println("Annotation finished.");

						List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
						for (int j = 0; j < sentences.size(); j++) {
							CoreMap sentence = sentences.get(j);
							String pts2 = sentence.get(CoreAnnotations.TextAnnotation.class);
							//						System.out.println(pts);
//							String pts_replaced = replaceFeatures(pts2,product_name,product_type,featureValuesByName);
							String pts_replaced = replaceFeatures(pts2,product_name,product_type,listFeatures);

//							System.out.println("PTS1: "+pts2);
//							System.out.println("PTS2: "+pts_replaced);
							boolean isBlabla = true;
							boolean hasProductName = false;
							boolean hasGlobalFeature=false;
							boolean hasFeature=false;
//							System.out.println("PN: "+product_name);
//							System.out.println("pts2: "+pts2);
							if(pts2.matches(".*\\b"+Pattern.quote(product_name)+"\\b.*")){
								hasProductName = true;
							}

							for (Feature feature : listFeatures) {
//							for (String fn : featureNames) {
								String fn = feature.name;
								hasFeature=false;
								if(pts2.matches(".*\\b"+Pattern.quote(fn.replaceAll("_", " "))+"\\b.*")){
									isBlabla = false;
									hasFeature=true;
									hasGlobalFeature=true;
								}
//								List<String> featureValues = featureValuesByName.get(fn);
								List<String> featureValues = feature.values;
								for (String fv : featureValues) {
									if(pts2.matches(".*\\b"+Pattern.quote(fv.replaceAll("_", " "))+"\\b.*")){
										isBlabla = false;
										hasFeature = true;
										hasGlobalFeature=true;
									}
								}
								if(hasFeature){
									if(!fnToSentenceWithFrequency.containsKey(fn+feature.featureId)){
										fnToSentenceWithFrequency.put(fn+feature.featureId, new HashMap<String,Integer>());
									}
									if(fnToSentenceWithFrequency.get(fn+feature.featureId).containsKey(pts_replaced)){
										fnToSentenceWithFrequency.get(fn+feature.featureId).put(pts_replaced, fnToSentenceWithFrequency.get(fn+feature.featureId).get(pts_replaced)+1);
									}
									else{
										fnToSentenceWithFrequency.get(fn+feature.featureId).put(pts_replaced, 1);
									}
								}
							}
							if(hasProductName && !hasGlobalFeature){
								if(productNameSentences.containsKey(pts_replaced)){
									productNameSentences.put(pts_replaced, productNameSentences.get(pts_replaced)+1);
								}
								else{
									productNameSentences.put(pts_replaced, 1);
								}
							}
							if(isBlabla){
								if(pts2.matches(".*\\d.*")){
									//TODO Maybe delete this part because sentences with digits are not being considered as BLABLA.
								}
								else{
									String []parts = pts2.trim().split(" ");
									if(parts.length>1 && parts.length<10){
										if(blablaSentences.containsKey(pts2)){
											blablaSentences.put(pts2, blablaSentences.get(pts2)+1);
										}
										else{
											blablaSentences.put(pts2, 1);
										}
									}
								}
							}
						}
					}
				}
				domainToBLABLA.put(type, blablaSentences);
				domainToProductNameSentences.put(type, productNameSentences);
//				System.out.println("GENERAL features:");
//				Set<String> kes = fnToSentenceWithFrequency.keySet();
//				for (String feature : kes) {
//					System.out.println("\t"+feature + "--"+fnToSentenceWithFrequency.get(feature).keySet().toString());
//					System.out.println("\tSentences:");
//					Set<String> kes4 = fnToSentenceWithFrequency.get(feature).keySet();
//					for (String feature2 : kes4) {
//						System.out.println("\t\t" + feature2);
//					}
//				}
//				System.out.println("BLABLA for: "+type);
//				Set<String> kes2 = blablaSentences.keySet();
//				for (String feature : kes2) {
//					System.out.println("\t"+feature);
//				}
//				System.out.println("PNs for: "+type);
//				Set<String> kes3 = productNameSentences.keySet();
//				for (String feature : kes3) {
//					System.out.println("\t"+feature);
//				}
				domainToFeatureToSentences.put(type, fnToSentenceWithFrequency);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	private String replaceFeatures(String input, String product_name, String product_type, List<Feature> listFeature) {
		String output = input.replaceAll("\\b"+Pattern.quote(product_name)+"\\b", "PRODUCTNAME");
		output = output.replaceAll("\\b"+Pattern.quote(product_type)+"\\b", "PRODUCTTYPE");		
		for (Feature feature : listFeature) {
			output = output.replaceAll("\\b"+Pattern.quote(feature.name.replace('_', ' '))+"\\b", "FEATURENAME"+feature.featureId);
			List<String> features = feature.values;
			for (String fv : features) {
				output = output.replaceAll("\\b"+Pattern.quote(fv)+"\\b", "FEATUREVALUE"+feature.featureId);
			}
		}
		return output;
	}

//	private String replaceFeatures(String input, String product_name, String product_type, HashMap<String, List<String>> featureValuesByName) {
//		String output = input.replaceAll("\\b"+Pattern.quote(product_name)+"\\b", "PRODUCTNAME");
//		output = output.replaceAll("\\b"+Pattern.quote(product_type)+"\\b", "PRODUCTTYPE");
//		Set<String> keys = featureValuesByName.keySet();
//		for (String fn : keys) {
//			output = output.replaceAll("\\b"+Pattern.quote(fn.replace('_', ' '))+"\\b", "FEATURENAME");
//			List<String> features = featureValuesByName.get(fn);
//			for (String fv : features) {
//				output = output.replaceAll("\\b"+Pattern.quote(fv)+"\\b", "FEATUREVALUE");
//			}
//		}
//		return output;
//	}

	public String realizeText(String type, String name, List<Feature> features){
		List<String> finalSentences = new LinkedList<String>();
		HashMap<String,HashMap<String,Integer>> featuresToSentences = domainToFeatureToSentences.get(type);
		HashMap<String,Integer> blablaSentences = domainToBLABLA.get(type);
		HashMap<String,Integer> productNameSentences = domainToProductNameSentences.get(type);
		String result = "";
		HashMap<String,Integer> sentencesForFeatures = new HashMap<String, Integer>();
		
		List<Feature> realFeatures = new LinkedList<Feature>();
		List<Feature> notSupportedFeatures = new LinkedList<Feature>();
		
		for (Feature f : features) {
//			System.out.println("NAME:"+ f.name);
			if(f.featureId==null){
				notSupportedFeatures.add(f);
				continue;
			}
			String featureKey = f.name+f.featureId;
			if(featuresToSentences.containsKey(featureKey)){
				HashMap<String, Integer> map = featuresToSentences.get(featureKey);
				Set<String> sentences = map.keySet();
				for (String sent : sentences) {
					int featureCounter=0;					
					Matcher m = Pattern.compile("\\bFEATUREVALUE\\d+\\b").matcher(sent);
					List<String> fValues = new ArrayList<String>();
					while (m.find()){
						if(!fValues.contains(m.group())){
							fValues.add(m.group());
						}
					}
					Matcher m2 = Pattern.compile("\\bFEATURENAME\\d+\\b").matcher(sent);
					List<String> fNames = new ArrayList<String>();
					while (m2.find()){
						if(!fNames.contains(m2.group())){
							fNames.add(m2.group());
						}
					}
					for (Feature f2 : features) {
						if(fNames.contains("FEATURENAME"+f2.featureId)){
							fNames.remove("FEATURENAME"+f2.featureId);
							featureCounter++;
						}
						if(fValues.contains("FEATUREVALUE"+f2.featureId)){
							fValues.remove("FEATUREVALUE"+f2.featureId);
							featureCounter++;
						}
					}
					if(fNames.isEmpty() && fValues.isEmpty()){
						if(!sentencesForFeatures.containsKey(sent)){
							sentencesForFeatures.put(sent, featureCounter);
						}
					}
				}
				
//				System.out.println("SENTENCES: "+sentence);
				realFeatures.add(f);
			}
			else{
				notSupportedFeatures.add(f);
			}
		}
		
		//System.out.println("List of all available sentences size: "+sentencesForFeatures.size());
		
		Map<String,Integer> orderedMap = sortByValue(sentencesForFeatures, true);
		Set<String> ked = sentencesForFeatures.keySet();
//		System.out.println("Suitable sentences:");
//		for (String string : ked) {
//			System.out.println("["+sentencesForFeatures.get(string)+"]" + string);
//		}
//		System.out.println("ordered");
		Set<String> orderedSentences = orderedMap.keySet();
		Iterator<String> orderedIterator = orderedSentences.iterator();
		List<Feature> auxFeatures = new LinkedList<Feature>();
//		System.out.println("REAL features size: "+realFeatures.size());
//		System.out.println("NSFList size: "+notSupportedFeatures.size());
		for (Feature feature : realFeatures) {
			auxFeatures.add(feature);
		}

		int pnPosition = (int)(Math.random()*productNameSentences.size());
		int counterPN = 0;
		Set<String> productNameKeys = productNameSentences.keySet();
		for (String string : productNameKeys) {
			if(counterPN==pnPosition){
				result += fillString(string, null, name, type);
				break;
			}
			counterPN++;
		}

		if(orderedIterator.hasNext()){
			String currentSentence = orderedIterator.next();
//			System.out.println("CS Out: "+currentSentence);
			while(!auxFeatures.isEmpty()){
				String auxResult = fillString(currentSentence, features, name, type);
				auxResult = auxResult.substring(0, 1).toUpperCase() + auxResult.substring(1);
				result += " * " + auxResult + "\n";

//				System.out.println("RESULT: "+result);
				Matcher fnm = Pattern.compile("\\bFEATURENAME\\d+\\b").matcher(currentSentence);
				while (fnm.find()){
					String fn = fnm.group();
					List<Feature> featuresToRemove = new LinkedList<Feature>();
					Iterator<Feature> itFeatures = auxFeatures.iterator();
					while(itFeatures.hasNext()){
						Feature fNext = itFeatures.next();
						if(fn.equals("FEATURENAME"+fNext.featureId)){
							featuresToRemove.add(fNext);
//							auxFeatures.remove(fNext);
//							System.out.println("REMOVING: "+fNext.featureId);
						}
					}
					for (Feature feature : featuresToRemove) {
						auxFeatures.remove(feature);
					}
				}
				Matcher fvm = Pattern.compile("\\bFEATUREVALUE\\d+\\b").matcher(currentSentence);
				while (fvm.find()){
					String fv = fvm.group();
					List<Feature> featuresToRemove = new LinkedList<Feature>();
					Iterator<Feature> itFeatures = auxFeatures.iterator();
					while(itFeatures.hasNext()){
						Feature fNext = itFeatures.next();
						if(fv.equals("FEATUREVALUE"+fNext.featureId)){
							featuresToRemove.add(fNext);
//							auxFeatures.remove(fNext);
//							System.out.println("REMOVING: "+fNext.featureId);
						}
					}
					for (Feature feature : featuresToRemove) {
						auxFeatures.remove(feature);
					}
				}
//				System.out.println("finished removing");
				if(auxFeatures.isEmpty()){
//					System.out.println("AuxFEATURES is EMPTY");
					break;
				}
				if(orderedIterator.hasNext()){
//					System.out.println("Iterator has next.1.");
					currentSentence = orderedIterator.next();
				}
//				System.out.println("SIZE OF AUXFEATURES: "+auxFeatures.size());
				while(!sentenceIsValidForFeatures(currentSentence,auxFeatures) && orderedIterator.hasNext()){
//					System.out.println("Loop finding next sentences.");
					currentSentence = orderedIterator.next();
				}
			}
		}

//		result += "\n";

		if(!notSupportedFeatures.isEmpty()){
			result += "\nWe are sorry but the next features are not supported by now. We just provided a bullet list: \n";
			for (Feature feature : notSupportedFeatures) {
				if(feature.currentValue!=null){
					result += " - "+"<span class=\"label label-primary\">"+feature.name+"</span>" + ": " + "<span class=\"label label-info\">"+feature.currentValue +"</span>" + "\n";
				}
				else{
					result += " - "+"<span class=\"label label-primary\">"+feature.name+"</span>" + "\n";
				}
			}
		}

//		result += "\n";

		int bbPosition = (int)(Math.random()*blablaSentences.size());
		int counter = 0;
		Set<String> blablaKeys = blablaSentences.keySet();
		for (String string : blablaKeys) {
			if(counter==bbPosition){
				string = string.substring(0, 1).toUpperCase() + string.substring(1);
				result += string;
				break;
			}
			counter++;
		}
		return result.trim();
	}

	private boolean sentenceIsValidForFeatures(String sent, List<Feature> auxFeatures) {
		Matcher m = Pattern.compile("\\bFEATUREVALUE\\d+\\b").matcher(sent);
		List<String> fValues = new ArrayList<String>();
		while (m.find()){
			if(!fValues.contains(m.group())){
				fValues.add(m.group());
			}
		}
		Matcher m2 = Pattern.compile("\\bFEATURENAME\\d+\\b").matcher(sent);
		List<String> fNames = new ArrayList<String>();
		while (m2.find()){
			if(!fNames.contains(m2.group())){
				fNames.add(m2.group());
			}
		}
		for (Feature f2 : auxFeatures) {
			if(fNames.contains("FEATURENAME"+f2.featureId)){
				fNames.remove("FEATURENAME"+f2.featureId);
			}
			if(fValues.contains("FEATUREVALUE"+f2.featureId)){
				fValues.remove("FEATUREVALUE"+f2.featureId);
			}
		}
		if(fNames.isEmpty() && fValues.isEmpty()){
			return true;
		}
		return false;
	}

//	public String realizeText_Old(String type, String name, List<Feature> features){
//		List<String> finalSentences = new LinkedList<String>();
//		HashMap<String,HashMap<String,Integer>> featuresToSentences = domainToFeatureToSentences.get(type);
//		String result = "";
//		for (Feature f : features) {
//			
//			//TODO Look for sentences that include more than one feature.
//						
////			System.out.println("NAME:"+ f.name);
//			String featureKey = f.name+f.featureId;
//			if(featuresToSentences.containsKey(featureKey)){
//				//TODO get one of the options based on probabilities and if it is containing another features or not.				
//				String sentence = "";
//				System.out.println("NUMBER OF SENTENCES: "+featuresToSentences.get(featureKey).size());
//				int position = (int)(Math.random()*featuresToSentences.get(featureKey).size());
//				System.out.println("RANDOM POSITION: "+position);
//				HashMap<String, Integer> map = featuresToSentences.get(featureKey);
//				int counter = 0;
//				Set<String> sentences = map.keySet();
//				for (String string : sentences) {
//					if(counter==position){
//						sentence = string;
//						break;
//					}
//					counter++;
//				}
//				System.out.println("SENTENCES: "+sentence);
//				result += " " + fillString(sentence, f, name, type);
//			}			
//		}
//		return result.trim();
//	}

	private String fillString(String sentence, List<Feature> features, String product_name, String product_type) {
		String result = sentence.replaceAll("PRODUCTTYPE", "<span class=\"label label-warning\">"+product_type+"</span>");
		result = result.replaceAll("PRODUCTNAME", "<span class=\"label label-danger\">"+product_name+"</span>");
		if(features!=null){
			for (Feature f : features) {
				result = result.replaceAll("FEATURENAME"+f.featureId, "<span class=\"label label-success\">"+f.name+"</span>");
				result = result.replaceAll("FEATUREVALUE"+f.featureId, "<span class=\"label label-success\">"+f.currentValue+"</span>");
			}
		}
		return result;
	}
	
	private String fillSentence(Sentence sentence, Feature f) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean reverse) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				if (reverse){
					return (int)((Math.random()*2000f)-1000);
					//return (o2.getValue()).compareTo(o1.getValue());
				}
				else{
					return (o1.getValue()).compareTo(o2.getValue());
				}
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
