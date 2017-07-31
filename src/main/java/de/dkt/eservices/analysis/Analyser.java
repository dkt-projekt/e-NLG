package de.dkt.eservices.analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.SemanticHeadFinder;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;

public class Analyser {

	static HashMap<String, HashMap<String, Double>> jjMap = new HashMap<String, HashMap<String, Double>>();
	HeadFinder hf = new SemanticHeadFinder();
	
	final LexicalizedParser parser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
	final Properties props = PropertiesUtils.asProperties("annotators", "tokenize,ssplit");//,pos,lemma");
	final StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	
	public String simpleCleaning(String input){
		return input.replaceAll("<[^>]+>", "\n");
	}
	

	
	public void addjectives(Tree t, String headNoun){
		
		for (Tree st : t.getChildrenAsList()){
			if (st.label().toString().equalsIgnoreCase("np")){
				headNoun = st.headTerminal(hf).toString();
			}
			if (st.isPhrasal()){
				addjectives(st, headNoun);
			}
			else {
				if (st.value().equalsIgnoreCase("jj")) {
//					String adj = st.getLeaves().toString();
//					HashMap<String, Double> im = jjMap.containsKey(adj) ? jjMap.get(adj) : new HashMap<String, Double>();
//					double d = im.containsKey(headNoun) ? im.get(headNoun) + 1 : 1;
//					im.put(headNoun, d);
//					jjMap.put(adj, im);
					String adj = st.getLeaves().toString();
					HashMap<String, Double> im = jjMap.containsKey(headNoun) ? jjMap.get(headNoun) : new HashMap<String, Double>();
					double d = im.containsKey(adj) ? im.get(adj) + 1 : 1;
					im.put(adj, d);
					jjMap.put(headNoun, im);
					
				}
			}
		}
	}
	
	public void normalizeJJMap(){
		for (String adj : jjMap.keySet()){
			HashMap<String, Double> im = jjMap.get(adj);
			double t = 0;
			for (String n : im.keySet()){
				t += im.get(n);
			}
			HashMap<String, Double> nm = new HashMap<String, Double>();
			for (String n : im.keySet()){
				nm.put(n, im.get(n) / t);
			}
			jjMap.put(adj, nm);
		}
	}
	
	public String[] readLines(String filename) throws IOException {
		FileReader fileReader = new FileReader(filename);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		List<String> lines = new ArrayList<String>();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			lines.add(line);
		}
		bufferedReader.close();
		return lines.toArray(new String[lines.size()]);
	}
	
	public ArrayList<String> tempSunnypetParsing(String dbdumppath){
		
		ArrayList<String> articles = new ArrayList<String>();
		HashMap<Integer, HashMap<Integer, String>> hm = new HashMap<Integer, HashMap<Integer, String>>();
		try {
			String[] flines = readLines(dbdumppath);
			int c = 0;
			for (String s : flines){
				String[] parts = s.split("\t");
				if (parts.length > 2) {
					try {
					int articleId = Integer.parseInt(parts[0]);
					String sentence = parts[1];
					int sentenceId = Integer.parseInt(parts[2]);
					HashMap<Integer, String> im = hm.containsKey(articleId) ? hm.get(articleId)	: new HashMap<Integer, String>();
					im.put(sentenceId, sentence);
					hm.put(articleId, im);
					}catch (NumberFormatException nfe){
						//System.out.println("WARNING: Could not parse something in line: " + s);
					}
				}
				else {
					c += 1;
				}
			}
			//System.out.println("WARNING: Skipped " + c + " lines.");
			
			for (int articleId : hm.keySet()){
				String article = "";
				for (int i = 0; i < hm.get(articleId).size(); i++){
					article += hm.get(articleId).get(i) + "\n";
				}
				articles.add(article.trim());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return articles;
	
	}
	
	public void populateJJMap(ArrayList<String> descriptions){

		
//		for(CoreMap sentence: sentences) {
//            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
//                lemmas.add(token.get(LemmaAnnotation.class));
		
		// make corenlp shut up:
		PrintStream err = System.err;
		System.setErr(new PrintStream(new OutputStream() {
		    public void write(int b) {
		    }
		}));
		
		for (String desc : descriptions){
			Annotation ann = new Annotation(desc);
			pipeline.annotate(ann);
			for (CoreMap sent : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
				Tree tree = parser.parse(sent.toString());
				addjectives(tree, "");
			}
		}
		normalizeJJMap();
		System.setErr(err);   
		
	}

	
	public static void main (String[] args){
		
//		PrintStream err = System.err;
//
//		System.setErr(new PrintStream(new OutputStream() {
//		    public void write(int b) {
//		    }
//		}));

		
		Analyser an = new Analyser();
		//String exampleInput = "kwmobile offers its customers a wide range of accesories for mobiles, tablets and laptops. Protective cases, electronic accesories, gadgets - kwmobile has been delivering great products to satisfied customers since 2012.<ul></ul><ul></ul><b>Details</b><ul></ul><ul><li>TRANSPARENT PROTECTION: The transparent case preserves the original look of your P8 Lite (2017). The crystal case made of resistant plastic protects against scratches and wear and tear.</li><li>ALMOST INVISIBLE PROTECTION: kwmobile covers are see-through and therefore preserve the original design of your smartphone.</li><li>SUPER HANDLING: The material is durable and shock-resistant. It lies optimally in the hand and underlines the unique design of your Huawei P8 Lite (2017).</li><li>LIGHT AND SLIM: The kwmobile cover is a lightweight among the Huawei P8 Lite (2017) covers and is particularly slim designed.</li><li>WE ARE ALWAYS THERE FOR YOU: When you buy a kwmobile product you also benefit from the help and assistance of our customer care service.</li></ul><b>Scope of delivery</b><ul></ul><ul><li>1x Crystal Hard Case for Huawei P8 Lite (2017)</li></ul>";
		//exampleInput = an.simpleCleaning(exampleInput);
		//ArrayList<String> articles = an.tempSunnypetParsing("C:\\Users\\pebo01\\Desktop\\sunnypetSentences.csv");
		String[] flines = null;
		try {
			flines = an.readLines("C:\\Users\\pebo01\\Desktop\\sentsTemp.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> articles = new ArrayList<String>();
		for (String s : flines){
			articles.add(an.simpleCleaning(s));
		}
		
		an.populateJJMap(articles);
		
		//System.out.println(jjMap);
		for (String jj : jjMap.keySet()){
			System.out.println(jj);
			for (String n : jjMap.get(jj).keySet()){
				System.out.println("\t" + n + "\t" + jjMap.get(jj).get(n));
			}
			System.out.println("\n");
		}
		
//		System.setErr(err);   
		
	}

}
