package de.dkt.eservices.enlg.features;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class FeatureExtractor {

	public HashMap<String, List<Feature>> domainToFeatures;

	public String folderPath = "jsonFiles/";
	
	public FeatureExtractor(){
		super();
	}
	
	@PostConstruct
	public void initializeModels(){
		domainToFeatures = new HashMap<String, List<Feature>>();
		String types[]= {"headphone","tv","phone"};
//		String types[]= {"headphone"};
		for (String type : types) {
			List<Feature> features= new LinkedList<Feature>();
			HashMap<String, List<String>> featureValuesByName = new HashMap<String, List<String>>();
			ClassPathResource cpr = new ClassPathResource(folderPath + type + ".json");
			//File f = new File(folderPath + type + ".json");
			try{
				File f = cpr.getFile();
//				System.out.println("PATH: "+f.getAbsolutePath());
				String jsonText = IOUtils.toString(new FileInputStream(f));
//				System.out.println("JSONTEXT: "+jsonText);
				JSONArray array = new JSONArray(jsonText);
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = (JSONObject)array.get(i);
//					String desc = "";
					Set<String> keys = json.keySet();
//					String product_name="",product_type="";
					for (String key : keys) {
						if(key.equalsIgnoreCase("product_name") ){
//							product_name = json.getString(key);
						}
						else if(key.equalsIgnoreCase("product_type") ){
//							product_type= json.getString(key);
						}
						else if(key.equalsIgnoreCase("description") ){
//							desc = json.getString(key);
						}
						else if(!key.equalsIgnoreCase("id") && !key.equalsIgnoreCase("product_gtin") && !key.equalsIgnoreCase("urls") &&
								!key.equalsIgnoreCase("model") && !key.equalsIgnoreCase("brand") && !key.equalsIgnoreCase("series")){
							if(!featureValuesByName.containsKey(key)){
								featureValuesByName.put(key, new LinkedList<String>());
							}
							String value = json.get(key).toString();
							if(!value.equalsIgnoreCase("")){
								if(!featureValuesByName.get(key).contains(value)){
									featureValuesByName.get(key).add(value);
								}
							}
						}
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
				System.out.println("ERROR: loading Feature Model for: " + type);
				System.exit(0);
			}
			int counter = 1000;
			Set<String> featureNames = featureValuesByName.keySet();
			for (String fn : featureNames) {
				Feature auxF = new Feature(counter+"",fn, featureValuesByName.get(fn), null);
				features.add(auxF);
				counter++;
			}
			domainToFeatures.put(type, features);
		}
	}
	
	public List<Feature> extractFeaturesFromString(String productType, String sFeatures){
		List<Feature> features= domainToFeatures.get(productType);
		
//		System.out.println("Domain features:");
//		for (Feature feature : features) {
//			System.out.println("\t"+feature.name + "--"+feature.values.toString()+"--"+feature.currentValue);
//		}

		List<Feature> productFeatures = new LinkedList<Feature>();
		
		String [] parts = sFeatures.split(",");
		for (String sFeature : parts) {
			sFeature = sFeature.trim();
			boolean isFound = false;
			for (Feature f : features) {
				//Check names of features
//				if(f.name.equalsIgnoreCase(sFeature) || f.name.matches("\\b"+Pattern.quote(sFeature)+"\\b")){
				if(f.name.equalsIgnoreCase(sFeature)){
					Feature auxF = new Feature(f.featureId,f.name, f.values, "yes");
//					Feature auxF = new Feature(f.name, f.values, "included");
					productFeatures.add(auxF);
					isFound=true;
				}

				//Check values of features.
				List<String> values = f.values;
				for (String value : values) {
					if(value.equalsIgnoreCase(sFeature) /*|| value.contains(sFeature)*/){
						Feature auxF = new Feature(f.featureId,f.name, f.values, sFeature);
						productFeatures.add(auxF);
						isFound=true;
						break;
					}
				}
			}
			if(!isFound){
				productFeatures.add(new Feature(null,sFeature,null,null));
			}
		}
		return productFeatures;
	}
	
}
