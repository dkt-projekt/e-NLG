package de.dkt.eservices.enlg.template;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import de.dkt.eservices.enlg.linguistic.LinguisticFeature;
import de.dkt.eservices.enlg.linguistic.Sentence;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

@Component
public class TemplateGenerator {

	private StanfordCoreNLP pipeline;

	@PostConstruct
	public boolean initializeModels(){
		Properties props;
		props = new Properties();
		//		props.put("annotators", "tokenize, ssplit, pos, lemma");
//		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, depparse, dcoref, natlog, openie");
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, depparse");
		this.pipeline = new StanfordCoreNLP(props);
		return true;
	}

	String[] featureNames = {"mdoel","aditional features","connectivity technology","controls","detachable cable","impedance","included accessories","headphones form factor","frequency response","microphone","foldable","microphone audio details","magnet material","microphone sensitivity","headphones cup type","max input power","sound output mode","compliant standards","diaphragm","thd"};

	HashMap<String, List<String>> featureSentencesMap = new HashMap<String, List<String>>();
	HashMap<String, List<String>> featureBlackedSentencesMap = new HashMap<String, List<String>>();
	HashMap<String, Float> feature2SubjectCount = new HashMap<String, Float>();
	static HashMap<String,List<String>> verbsForFeatures = new HashMap<String, List<String>>();
	static List<String> verbsForPN = new LinkedList<String>();
	static List<String> verbsForPT = new LinkedList<String>();
	static List<String> blablaSentences = new LinkedList<String>();

	
	public Template generateTemplate(List<String> texts){
		Template t = new Template();

		/**
		 * Analyze linguistically every texts: 
		 *   - tokenization, sentence splitting, PoS tagging, Dependency Parsing, etc....
		 */
		List<Annotation> annotations = new LinkedList<Annotation>();
		for (String text : texts) {
			Annotation document = new Annotation(text);
			pipeline.annotate(document);
			annotations.add(document);
		}

		for (String string : featureNames) {
			featureSentencesMap.put(string,new LinkedList<String>());
			featureBlackedSentencesMap.put(string,new LinkedList<String>());
			feature2SubjectCount.put(string,0f);
		}
		/**
		 * Use the extracted information for classifying the sentences between KEY or BLABLA 
		 * Define
		 * 	 - Define features and values included in the texts.
		 *   - Set structure of KEY sentences
		 *   - Set structure of BLABLA sentences
		 *   - Order of sentences (KEY, BLABLA, etc.)
		 *   - Order of presented features inside the KEY sentences.
		 */
		HashMap<String, Integer> frequency = new HashMap<String, Integer>();
		HashMap<String, String> examples = new HashMap<String, String>();
		HashMap<String,List<String>> sentenceStructures = new HashMap<String, List<String>>();
		HashMap<String,List<String>> sentenceClassifications= new HashMap<String, List<String>>();
		HashMap<String,HashMap<String,String>> featuresClassification = new HashMap<String, HashMap<String,String>>();
		for (Annotation annotation : annotations) {
			List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
			List<String> order = new LinkedList<String>();
			List<String> structures = new LinkedList<String>();
			HashMap<String,String> features = new HashMap<String,String>();
			for (int i = 0; i < sentences.size(); i++) {
				CoreMap sentence = sentences.get(i);
				for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//					int tokenStart = token.beginPosition();
//					int tokenEnd = token.endPosition();
				}
				String plainTextSentence = sentence.get(CoreAnnotations.TextAnnotation.class);
				System.out.println(plainTextSentence);
//				for (String string : featureNames) {
//					if(plainTextSentence.contains(string)){
//						featureSentencesMap.get(string).add(plainTextSentence);
//						String newTextSentence = plainTextSentence.replaceAll(string, "##FEATURE##");
//						featureBlackedSentencesMap.get(string).add(newTextSentence);
//					}
//				}
				
				boolean isKEY = false;
				String structure = "";
				
				Tree tree = sentence.get(TreeAnnotation.class);
//				System.out.println("TREE: "+tree.pennString());
//				getNPFromTree(tree);
				tree.pennPrint();
				
				SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
				dependencies.prettyPrint();

//				SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
//				System.out.println("PLAINTEXT: "+plainTextSentence);

//				String relations = "";
//				Collection<TypedDependency> tds = dependencies.typedDependencies();
//				for (TypedDependency td : tds) {
////					System.out.println("DEP: "+td.dep());
////					System.out.println("RELN: "+td.reln());
////					System.out.println("GOV: "+td.gov());
//					relations += " " + td.reln();
//				}
				
//				System.out.println("parse tree:\n" + tree.pennString());
//				printTAGtree(tree.toString(), "", 0);
//				TreeNode<String,String> node = new TreeNode<String, String>();
//				generateTAGtree(tree.toString(), node);
//				node.printNode(" ");
				//TODO Features
//				printLeaflessTree(tree);
//				String flatTree = getLeaflessTree(tree);
//				System.out.println(flatTree);

//				String flatTree = relations;
//				if(frequency.containsKey(flatTree)){
//					frequency.put(flatTree, frequency.get(flatTree)+1);
//				}
//				else{
//					frequency.put(flatTree, 1);
//					examples.put(flatTree, sentence.get(CoreAnnotations.OriginalTextAnnotation.class));
//				}
				
				
				
//				System.out.println("LABELS: "+tree.labels());
//				System.out.println("PTY: "+tree.preTerminalYield().toString());
//				System.out.println("PTY: "+tree.preTerminalYield().toString());
//				System.out.println();
				features.put("", "");
				if(isKEY){
					order.add("KEY");
				}
				else{
					order.add("BLABLA");
				}
				structures.add(structure);
			}
			sentenceClassifications.put(annotation.toString(), order);
			sentenceStructures.put(annotation.toString(), structures);
			featuresClassification.put(annotation.toString(), features);
		}
		
//		Map<String, Integer> frequencyOrdered = sortByValue(frequency, true);
//		
//		for (int j = 0; j < 5; j++) {
//			System.out.println(frequencyOrdered.keySet().toArray()[j]);
//			System.out.println("\t"+frequencyOrdered.get(frequencyOrdered.keySet().toArray()[j]));
//		}

		/**
		 * Define template 
		 * 	 - Define features and values included in the texts.
		 */
		for (Annotation ann : annotations){
			List<CoreMap> sentences = ann.get(CoreAnnotations.SentencesAnnotation.class);
			for (int i = 0; i < sentences.size(); i++) {
				CoreMap sentence = sentences.get(i);
				String plainTextSentence = sentence.get(CoreAnnotations.TextAnnotation.class);
				System.out.println(plainTextSentence);

				SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
				dependencies.prettyPrint();
				for (TypedDependency td : dependencies.typedDependencies()){
					System.out.println("debugging td:"  + td);
//					System.out.println("gov:" + td.gov());
//					System.out.println("dep:" + td.dep());
					// WARNING: too much looping going on here, restructure?
					if (td.reln().toString().equalsIgnoreCase("nsubj")){
						for (String fn : featureNames){
							if (td.dep().word().toLowerCase().contains(fn.toLowerCase())){
								feature2SubjectCount.put(fn, feature2SubjectCount.get(fn)+1);
								// TODO: this is too broad. Get indices of dep in complete sentence, then check if the surrounding words are also part of the feature name!!!
							}
							else if (fn.toLowerCase().contains(td.dep().word().toLowerCase())){
								feature2SubjectCount.put(fn, feature2SubjectCount.get(fn)+1);
							}
						}
					}
				}
			}
		}
		
//		for (String string : featureNames) {
//			System.out.println(string);
//			System.out.println("\t--"+feature2SubjectCount.get(string));
//			List<String> sents = featureBlackedSentencesMap.get(string);
//			for (String string2 : sents) {
//				System.out.println("\t"+string2);
//			}
//		}
		

		
//		for (Annotation annotation : annotations) {
//			System.out.println("=============================");
//			List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
//			for (int i = 0; i < sentences.size(); i++) {
//				System.out.println("<<<<<<<<<<<<<<<<<<<<<");
//				CoreMap sentence = sentences.get(i);
//				for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//					System.out.println(token.originalText()+"["+token.lemma()+"]"+"["+token.ner()+"]"+"["+token.tag()+"]");
//					int tokenStart = token.beginPosition();
//					int tokenEnd = token.endPosition();
//				}
//				System.out.println("<<<<<<<<<<<<<<<<<<<<<");
//				Tree tree = sentence.get(TreeAnnotation.class);
//				System.out.println("parse tree:\n" + tree);
////				printtree(tree.toString(), "");
//
//				System.out.println("<<<<<<<<<<<<<<<<<<<<<");
//				// this is the Stanford dependency graph of the current sentence
//				SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
//				System.out.println("dependency graph:\n" + dependencies);
//			}
////			// This is the coreference link graph
////		    // Each chain stores a set of mentions that link to each other,
////		    // along with a method for getting the most representative mention
////		    // Both sentence and token offsets start at 1!
////		    Map<Integer, CorefChain> graph = 
////		        annotation.get(CorefChainAnnotation.class);
////		    Set<Integer> keys = graph.keySet();
////		    for (Integer integer : keys) {
////				System.out.println("\t"+graph.get(integer));
////			}
//		}

		
		
		return t;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean reverse) {
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

	class TreeNode<K,V> {
		public K key;
		public V value;
		List<TreeNode<K,V>> nodes=new LinkedList<>();
		
		public void addChild(TreeNode<K, V> node){
			nodes.add(node);
		}
		
		public void printNode(String indent){
			System.out.println(indent+key+":"+value);
			for (TreeNode<K, V> treeNode : nodes) {
				treeNode.printNode(indent+" ");
			}
		}
	}
	
	public int generateTAGtree(String s, TreeNode<String,String> node){
		String aux = "";
//		System.out.println("entering: "+s);
//		node.printNode("  ");
		for (int i = 0; i < s.length(); i++) {
//			System.out.println(i);
			char a = s.charAt(i);
			if(a=='('){
				if(node.key==null){
					node.key = aux.trim();
				}
				aux="";
				TreeNode<String, String> newNode = new TreeNode<String, String>();
				int position = generateTAGtree(s.substring(i+1),newNode);
				node.addChild(newNode);
				if(position==-1){
					return -1;
				}
				i = i + position;
				
//				if(aux.trim().contains(" ")){
//					String parts[] = aux.trim().split(" ");
//					newNode.key = parts[0];
//					newNode.value = parts[1];
//				}
//				else{
//					newNode.key = aux.trim();
//				}
////				System.out.println(s2+""+aux.trim()+"");
//				int newi = 0;
//				if(aux.trim().equalsIgnoreCase("")){
//					newi = generateTAGtree(s.substring(i+1),node);
//				}
//				else{
//					newi = generateTAGtree(s.substring(i+1),newNode);
//					node.addChild(newNode);
//				}
//				aux="";
//				if(newi==-1){
//					return -1;
//				}
////				else if(newi==0){
////					newi=1;//return 0;
////				}
//				return i;
			}
			else if(a==')'){
				if(aux.trim().contains(" ")){
					String parts[] = aux.trim().split(" ");
					node.key = parts[0];
					node.value = parts[1];
					aux="";
//					System.out.println("returning first: "+i);
					return i+1;
				}
				else{
//					System.out.println("returning second: "+i);
					return i+1;
				}
//				node.key = aux.trim();
//				TreeNode<String, String> newNode = new TreeNode<String, String>();
//				int position = generateTAGtree(s.substring(i+1),newNode);
//				node.addChild(newNode);
//				i = i + position;
//				if(i==0){
//					System.out.println("returning first: 0");
//					return 0;
//				}
//				TreeNode<String, String> newNode = new TreeNode<String, String>();
//				if(aux.trim().contains(" ")){
//					String parts[] = aux.trim().split(" ");
//					newNode.key = parts[0];
//					newNode.value = parts[1];
//					node.addChild(newNode);
//					aux="";
//					return i;
////					int newi = generateTAGtree(s.substring(i+1),node);
////					if(newi==-1){
////						System.out.println("returning: -1");
////						return -1;
////					}
////					else if(newi==0){
////						continue;
////					}
////					i = i + newi;
//////					return i;
//				}
//				else{
//					System.out.println("returning: "+(i));
//					return i;
////					newNode.key = aux.trim();
//				}
////				if(!aux.trim().equalsIgnoreCase("")){
////					System.out.print(s2+""+aux.trim()+"");
////				}
////				printTAGtree(s.substring(i+1),s2.substring(1),open-1);
			}
			else{
				aux += a;
			}
		}
		return -1;
	}

	public void printTAGtree(String s, String s2, int open){
		String aux = "";
		for (int i = 0; i < s.length(); i++) {
			char a = s.charAt(i);
			if(a=='('){
				System.out.println(s2+""+aux.trim()+"");
				printTAGtree(s.substring(i+1),s2+" ",open+1);
				return;
			}
			else if(a==')'){
				if(!aux.trim().equalsIgnoreCase("")){
					System.out.print(s2+""+aux.trim()+"");
				}
				printTAGtree(s.substring(i+1),s2.substring(1),open-1);
				return;
			}
			else{
				aux += a;
			}
		}
	}

	public void printtree(String s, String s2){
		String aux = "";
		for (int i = 0; i < s.length(); i++) {
			char a = s.charAt(i);
			if(a=='('){
				System.out.println(s2+"("+aux+"");
				printtree(s.substring(i+1),s2+" ");
				return;
			}
			else if(a==')'){
				System.out.println(s2+""+aux+")");
				printtree(s.substring(i+1),s2.substring(1));
				return;
			}
			else{
				aux += a;
			}
		}
	}

	public String getLeaflessTree(Tree t){
		String s = "";
		List<Tree> trees = t.getChildrenAsList();
		s = "(";
		s += t.label()+" ";
		if(!t.isPreTerminal() && !t.isLeaf() && !t.isPrePreTerminal()){
			for (Tree tree : trees) {
				s += getLeaflessTree(tree);
			}
		}
		s +=")";
		return s;
	}
	
	public boolean getNPFromTree(Tree t){
		List<Tree> trees = t.getChildrenAsList();
		boolean done = false;
		for (Tree tree : trees) {
			boolean done2 = getNPFromTree(tree); 
			done = (done)?done:done2;
		}
		if(!done){
			if(t.label().toString().equalsIgnoreCase("NP")){
				ArrayList<Label> words = t.yield();
				String s = "";
				for (Label label : words) {
					s += " " + label.toString().substring(0, label.toString().lastIndexOf('-'));
				}
				s = s.trim();
				for (String featureName : featureNames) {
					if(s.toLowerCase().contains(featureName)){
						featureSentencesMap.get(featureName).add(s.toLowerCase());
						String newTextSentence = s.toLowerCase().replaceAll(featureName, "##FEATURE##");
						featureBlackedSentencesMap.get(featureName).add(newTextSentence);
						System.out.println("\t"+newTextSentence);
					}
				}
				return true;
			}
			return false;
		}
		return done;
	}
	
	public static void main(String[] args) throws Exception{
		TemplateGenerator tg = new TemplateGenerator();
		tg.initializeModels();

		HashMap<String, List<String>> featureValuesByName = new HashMap<String, List<String>>();
		HashMap<JSONObject,JSONObject> templates = new HashMap<JSONObject, JSONObject>();
		String jsonText = IOUtils.toString(new FileInputStream("/Users/jumo04/Downloads/json/HeadphoneCatalog-1.json"));
//		JSONObject json = new JSONObject(jsonText);
		JSONArray array = new JSONArray(jsonText);

		for (int i = 0; i < array.length(); i++) {
			JSONObject json = (JSONObject)array.get(i);
			String desc = "";
			Set<String> keys = json.keySet();
			String product_name="",product_type="";
			for (String key : keys) {
				if(key.equalsIgnoreCase("product_name") ){
					product_name = json.getString(key);
				}
				else if(key.equalsIgnoreCase("product_type") ){
					product_type= json.getString(key);
				}
				else if(key.equalsIgnoreCase("description") ){
					desc = json.getString(key);
				}
				else if(!key.equalsIgnoreCase("id")
						&& !key.equalsIgnoreCase("product_gtin") && !key.equalsIgnoreCase("urls")
						){
					if(!featureValuesByName.containsKey(key)){
						featureValuesByName.put(key, new LinkedList<String>());
					}
					String value = json.getString(key);
					if(!value.equalsIgnoreCase("")){
						if(!featureValuesByName.get(key).contains(value)){
							featureValuesByName.get(key).add(value);
						}
					}
				}
			}
		}
		for (int i = 0; i < array.length(); i++) {
			JSONObject json = (JSONObject)array.get(i);
			String desc = json.getString("description");
				if(!desc.equalsIgnoreCase("")){
				HashMap<String, String> features = new HashMap<String, String>();
				Set<String> keys = json.keySet();
				String product_name="",product_type="";
				for (String key : keys) {
					if(key.equalsIgnoreCase("product_name") ){
						product_name = json.getString(key);
					}
					else if(key.equalsIgnoreCase("product_type") ){
						product_type= json.getString(key);
					}
					else if(!key.equalsIgnoreCase("description") && !key.equalsIgnoreCase("id")
							&& !key.equalsIgnoreCase("product_gtin") && !key.equalsIgnoreCase("urls")
							){
						String value = json.getString(key);
						if(!value.equalsIgnoreCase("")){
							features.put(key.replaceAll("_", " "), value);
						}
					}
//					features.put(key, json.getString(key));
				}				
				//TODO Include all the features in the JSON inside the HashMap.
				Template t = tg.generateTemplateFromSingleText(desc,product_name,product_type,features,featureValuesByName);
				JSONObject output = t.getJSONObject();
				templates.put(json, output);
			}
		}
	
		Set<String> keys = verbsForFeatures.keySet();
		for (String key : keys) {
			System.out.println(key);
			for (String string : verbsForFeatures.get(key)) {
				System.out.println("\t"+string);
			}
		}

		System.out.println("BLABLA - Sentences");
		for (String bb: blablaSentences) {
			System.out.println(bb);
		}
		
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/jumo04/Downloads/headphoneDescriptions.txt"), "utf-8"));
//		List<String> texts = new LinkedList<String>();
//		String line = br.readLine();
//		while(line!=null){
//			if(!line.equalsIgnoreCase("")){
//				texts.add(line);
//			}
//			line = br.readLine();
//		}
//		
//		tg.generateTemplate(texts);
	}

	private Template generateTemplateFromSingleText(String desc, String productName, String productType, 
			HashMap<String, String> features, HashMap<String, List<String>> featureValuesByName) {
		Template t = new Template();
		
		String desc2 = desc.replaceAll("\\b"+productName+"\\b", "PRODUCTNAME");
		desc2 = desc2.replaceAll("\\b"+productType+"\\b", "PRODUCTTYPE");

		Set<String> keys = features.keySet();
		for (String string : keys) {
			desc2 = desc2.replaceAll("\\b"+string+"\\b", "FEATURENAME");
			desc2 = desc2.replaceAll("\\b"+features.get(string)+"\\b", "FEATUREVALUE");
		}
		
		Annotation annotation = new Annotation(desc);
		pipeline.annotate(annotation);
		Annotation annotation2 = new Annotation(desc2);
		pipeline.annotate(annotation);

		List<Sentence> sentencesObjects = new LinkedList<Sentence>();
		
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		List<String> order = new LinkedList<String>();
		List<String> structures = new LinkedList<String>();
		for (int i = 0; i < sentences.size(); i++) {
			List<LinguisticFeature> linguisticFeatures = new LinkedList<LinguisticFeature>();
			
			CoreMap sentence = sentences.get(i);
			for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
				//					int tokenStart = token.beginPosition();
				//					int tokenEnd = token.endPosition();
			}
			String pts = sentence.get(CoreAnnotations.TextAnnotation.class);
			System.out.println(pts);
			
//			if (!pts.contains("PRODUCTNAME") && !pts.contains("PRODUCTTYPE") && !pts.contains("FEATURENAME") && !pts.contains("FEATUREVALUE")){
//				System.out.println("SENTENCE IS CONTENTLESS SUGAR.");
//			}
//			
			
			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			List<TypedDependency> verbDeps = new LinkedList<TypedDependency>();
			for (TypedDependency td : dependencies.typedDependencies()){
				if (td.reln().toString().equalsIgnoreCase("nsubj")){
					if (td.gov().tag().toLowerCase().startsWith("v")){
						verbDeps.add(td);
					}
//					for (String fn : featureNames){
//						if (td.dep().word().toLowerCase().contains(fn.toLowerCase())){
//							feature2SubjectCount.put(fn, feature2SubjectCount.get(fn)+1);
//							// TODO: this is too broad. Get indices of dep in complete sentence, then check if the surrounding words are also part of the feature name!!!
//						}
//						else if (fn.toLowerCase().contains(td.dep().word().toLowerCase())){
//							feature2SubjectCount.put(fn, feature2SubjectCount.get(fn)+1);
//						}
//					}
				}
			}
			Set<String> keys2 = featureValuesByName.keySet();
			for (String key : keys2) {
				if(!verbsForFeatures.containsKey(key)){
					verbsForFeatures.put(key, new LinkedList<String>());
				}
			}
			if(pts.matches(".*\\b"+productName+"\\b.*")){
				for (TypedDependency td : verbDeps) {
					verbsForPN.add(td.gov().lemma());
					//TODO maybe check if the td.dev() contains a piece of the string of the PN.
				}
			}
			if(pts.matches(".*\\b"+productType+"\\b.*")){
				for (TypedDependency td : verbDeps) {
					verbsForPT.add(td.gov().lemma());
					//TODO maybe check if the td.dev() contains a piece of the string of the PT.
				}
			}
			boolean isBlabla = true;
			for (String fn : keys2) {
				if(pts.matches(".*\\b"+fn+"\\b.*")){
//					System.out.println("MATCH: "+fn);
					isBlabla = false;
					for (TypedDependency td : verbDeps) {
						verbsForFeatures.get(fn).add(td.gov().lemma());
						//TODO maybe check if the td.dev() contains a piece of the string of the FN.
					}
				}
				List<String> featureValues = featureValuesByName.get(fn);
				for (String fv : featureValues) {
					if(pts.matches(".*\\b"+fv+"\\b.*")){
						isBlabla = false;
//						System.out.println("MATCH2: "+fv);
						for (TypedDependency td : verbDeps) {
							verbsForFeatures.get(fn).add(td.gov().lemma());
							//TODO maybe check if the td.dev() contains a piece of the string of the FV.
						}
					}
				}
			}
			if(isBlabla){
				if(pts.matches(".*\\d.*")){
					
				}
				else{
					blablaSentences.add(pts);
				}
			}
			Tree tree = sentence.get(TreeAnnotation.class);
//			tree.pennPrint();
//			dependencies.prettyPrint();
			
			Sentence s1 = new Sentence();
			s1.features = linguisticFeatures;
		}
		t.objectName = "template";
		t.sentences = sentencesObjects;
		return t;
	}


}
