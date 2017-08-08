package de.dkt.eservices.enlg.linguistic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

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

public class Paraphrasing {

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

	HashMap<String, List<String>> paraphrases = new HashMap<String, List<String>>();

	public void initializePPDB() throws Exception {
		System.out.print("Initializing PPDB...");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/jumo04/Downloads/ppdb-1.0-s-all"), "utf-8"));
		String line = br.readLine();
		while(line!=null){
//			System.out.println(line);
			String parts[]= line.split("\\|\\|\\|");
			//System.out.println(parts[1].trim() + "-->" + parts[2].trim());
			String orig = parts[1].trim();
			String dest = parts[2].trim();
			if(!paraphrases.containsKey(orig)){
				paraphrases.put(orig, new LinkedList<String>());
			}
			paraphrases.get(orig).add(dest);
			line = br.readLine();
		}
		br.close();
		System.out.println("DONE");
	}
	
	public static void main(String[] args) throws Exception{
		Paraphrasing pp = new Paraphrasing();
		pp.initializeModels();
		Date d1 = new Date();
		pp.initializePPDB();
		Date d2 = new Date();
		
		System.out.println("Loading the paraphraseDB took: "+(d2.getTime()-d1.getTime())+" milliseconds");
		String sSentence = "the unique collapsible design, combined with swiveling ear cups, offers maximum flexibility in any application.";
		pp.paraphraseSentence(sSentence);
		
	}
	
	
	public String paraphraseSentence(String sent){
//		Sentence paraphrased = new Sentence();
		
		Annotation annotation = new Annotation(sent);
		pipeline.annotate(annotation);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		List<String> order = new LinkedList<String>();
		List<String> structures = new LinkedList<String>();
		for (int i = 0; i < sentences.size(); i++) {
			CoreMap sentence = sentences.get(i);
			String pts = sentence.get(CoreAnnotations.TextAnnotation.class);
			System.out.println(pts);
			for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
				//					int tokenStart = token.beginPosition();
				//					int tokenEnd = token.endPosition();
			}
			
			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			List<TypedDependency> verbDeps = new LinkedList<TypedDependency>();
			for (TypedDependency td : dependencies.typedDependencies()){
				if (td.reln().toString().equalsIgnoreCase("nsubj")){
					if (td.gov().tag().toLowerCase().startsWith("v")){
						verbDeps.add(td);
					}
				}
			}
			Tree tree = sentence.get(TreeAnnotation.class);
			Tree tree2 = tree.deepCopy();
			System.out.println("debugging flat tree:" + tree.flatten());			

			String flatten = tree.flatten().toString();

			List<String> list = new LinkedList<String>();
			generateStringList(flatten, list);
			System.out.println("FLATTEN: ");
			for (String string : list) {
				System.out.print(string + " ");
			}
			System.out.println();
			
//			getNPFromTree(tree2);
			//			tree.pennPrint();
//			dependencies.prettyPrint();
		}
		String paraphrased = "";
		return paraphrased;
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
				return true;
			}
			return false;
		}
		return done;
	}

	public String rephraseNP(List<String> words){
		String result = "";
	
		String totalText = "";
		for (String word : words) {
			totalText += " " + word;
		}
		System.out.println("<><><><><><><><><><><><>");
		System.out.println("Paraphrasing: "+totalText);
		if(paraphrases.containsKey(totalText)){
			for (String pps : paraphrases.get(totalText)) {
				System.out.println("\t"+pps);
				result = pps;
			}
		}
		else{
			for (String word : words) {
				System.out.println("\tPARAPHRASING WORD: "+word);
				if(paraphrases.containsKey(word)){
					String option = "";
					for (String pps : paraphrases.get(word)) {
						System.out.println("\t\t"+pps);
						option = pps;
					}
					result += " "+ option;
				}
				else{
					result += " "+ word;
				}
			}
		}
		System.out.println("<><><><><><><><><><><><>");
		return result;
	}

	public int generateStringList(String s, List<String> list){
		String aux = "";
		boolean inNP = false;
		List<String> newList = new LinkedList<String>();
		for (int i = 0; i < s.length(); i++) {
			char a = s.charAt(i);
			if(a=='('){
				if(inNP || aux.contains("NP")){
					inNP = true;
//					System.out.println("that is NP: "+s.substring(i+1));
					aux="";
					int position = generateStringList(s.substring(i+1),newList);
					if(position==-1){
						return -1;
					}
					i = i + position;				
				}
				else{
					aux="";
					int position = generateStringList(s.substring(i+1),list);
					if(position==-1){
						return -1;
					}
					i = i + position;				
				}
			}
			else if(a==')'){
				if(inNP || aux.contains("NP")){
//					list.addAll(newList);
					String output = rephraseNP(newList);
					list.add(output);
//					System.out.println("NP LIST: "+newList);
					return i+1;
				}
				else{
					if(aux.trim().contains(" ")){
						String parts[] = aux.trim().split(" ");
						list.add(parts[1]);
						aux="";
					}
					return i+1;
				}
			}
			else{
				aux += a;
			}
		}
		return -1;
	}

}
