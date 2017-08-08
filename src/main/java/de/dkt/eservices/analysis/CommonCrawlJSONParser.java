package de.dkt.eservices.analysis;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CommonCrawlJSONParser {
	
	
	public HashMap<String, HashMap<String, String>> JSON2Hash(String jsonFilePath){
		
		JSONParser parser = new JSONParser();
		HashMap<String, HashMap<String, String>> hm = new HashMap<String, HashMap<String, String>>();
		try {
			
			JSONArray a = (JSONArray) parser.parse(new FileReader(jsonFilePath));
			for (Object o : a) {
				JSONObject jo = (JSONObject) o;
				HashMap<String, String> im = new HashMap<String, String>();
				for (Object o2 : jo.keySet()){
					im.put(o2.toString(),  jo.get(o2).toString());
				}
				hm.put(jo.get("id").toString(), im);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hm;
		
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, final boolean reverse) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				if (reverse){
					return (o2.getValue()).compareTo(o1.getValue());
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
	
	
	public HashMap<String, ArrayList<String>> getTopNAttributes(String folderPath, int n){
		
		HashMap<String, ArrayList<String>> attributes = new HashMap<String, ArrayList<String>>();
		
		File df = new File(folderPath);
		HashMap<String, Double> attribute2count = new HashMap<String, Double>();
		HashMap<String, HashMap<String, HashMap<String, String>>> memoryMap =  new HashMap<String, HashMap<String, HashMap<String, String>>>(); // a lot of hashing :)
		double numDocs = 0;
		// first round for populating memoryMap and counting totals
		for (File f : df.listFiles()){
			HashMap<String, HashMap<String, String>> hm = JSON2Hash(f.getAbsolutePath());
			for (String id : hm.keySet()){
				numDocs++;
				for (String attr : hm.get(id).keySet()){
					double c = attribute2count.containsKey(attr) ? attribute2count.get(attr) + 1 : 1;
					attribute2count.put(attr, c);
				}
			}
			memoryMap.put(f.getAbsolutePath(), hm);
		}
		// second round for normalisation etc.
		for (String fp : memoryMap.keySet()){
			HashMap<String, HashMap<String, String>> im = memoryMap.get(fp);
			HashMap<String, Double> attributeMap = new HashMap<String, Double>();
			for (String id : im.keySet()){
				HashMap<String, String> im2 = im.get(id);
				for (String attr : im2.keySet()){
					double tfIdfVal = Math.log(numDocs / attribute2count.get(attr));// tf is always one, so val is actually just idf... (TODO: check if this influences the expressivity of tf.idf a lot)
					attributeMap.put(attr, tfIdfVal);
				}
			}
			attributeMap = (HashMap)sortByValue(attributeMap, true);
			int i = 0;
			ArrayList<String> temp = new ArrayList<String>();
			for (String s : attributeMap.keySet()){
				temp.add(s);
				i++;
				if (i == n){
					break;
				}
			}
			attributes.put(fp, temp);
			
		}
		
		// TODO: get importance of features from number of times slot has been filled (less important features will often be null I guess)
		
		return attributes;
		
	}
	
	
	public static void main (String[] args){
		
		CommonCrawlJSONParser jp = new CommonCrawlJSONParser();
		Analyser an = new Analyser();
		
		HashMap<String, ArrayList<String>> result = jp.getTopNAttributes("/Users/jumo04/Downloads/json/", 200);
		for (String key : result.keySet()){
			System.out.println(key);
			for (String attr : result.get(key)){
				System.out.println("\t" + attr);
			}
			System.out.println("\n");
		}
		
//		HashMap<String, HashMap<String, String>> headphoneMap = jp.JSON2Hash("C:\\Users\\pebo01\\Desktop\\data\\commonCrawl\\json\\TVCatalog.json");
//		
//		ArrayList<String> headphoneDescriptions = extractDescriptions(headphoneMap);
//		for (String s : headphoneDescriptions){
//			System.out.println(s);
//
//			
//		}
		
	}

	private static ArrayList<String> extractDescriptions(HashMap<String, HashMap<String, String>> jsonMap) {

		ArrayList<String> retlist = new ArrayList<String>();
		
		for (String id : jsonMap.keySet()){
			HashMap<String, String> im = jsonMap.get(id);
			String descr = im.containsKey("description") ? im.get("description") : null;
			if (descr != null){
				retlist.add(descr);
			}
		}
		
		return retlist;
	}

}
