package de.dkt.eservices.enlg.template;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
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
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, depparse, dcoref, natlog, openie");
		this.pipeline = new StanfordCoreNLP(props);
		return true;
	}

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

		/**
		 * Use the extracted information for classifying the sentences between KEY or BLABLA 
		 * Define
		 * 	 - Define features and values included in the texts.
		 *   - Set structure of KEY sentences
		 *   - Set structure of BLABLA sentences
		 *   - Order of sentences (KEY, BLABLA, etc.)
		 *   - Order of presented features inside the KEY sentences.
		 */
		HashMap<String,List<String>> sentenceStructures = new HashMap<String, List<String>>();
		HashMap<String,List<String>> sentenceClassifications= new HashMap<String, List<String>>();
		HashMap<String,HashMap<String,String>> featuresClassification = new HashMap<String, HashMap<String,String>>();
		for (Annotation annotation : annotations) {
			List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
			List<String> order = new LinkedList<String>();
			List<String> structures = new LinkedList<String>();
			HashMap<String,String> features = new HashMap<String,String>();
			for (int i = 0; i < sentences.size(); i++) {
				boolean isKEY = false;
				String structure = "";
				
				CoreMap sentence = sentences.get(i);
				for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//					int tokenStart = token.beginPosition();
//					int tokenEnd = token.endPosition();
				}
				Tree tree = sentence.get(TreeAnnotation.class);
				SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
				System.out.println("parse tree:\n" + tree);
//				printTAGtree(tree.toString(), "", 0);
				TreeNode<String,String> node = new TreeNode<String, String>();
				generateTAGtree(tree.toString(), node);
				node.printNode(" ");
				//TODO Features
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
		
		System.exit(0);
		/**
		 * Define template 
		 * 	 - Define features and values included in the texts.
		 */
		
		
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
	
}
