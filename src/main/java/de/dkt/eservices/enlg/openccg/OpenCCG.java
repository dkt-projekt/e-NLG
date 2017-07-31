package de.dkt.eservices.enlg.openccg;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.stereotype.Component;

import opennlp.ccg.grammar.Grammar;
import opennlp.ccg.hylo.HyloHelper;
import opennlp.ccg.hylo.Nominal;
import opennlp.ccg.lexicon.Tokenizer;
import opennlp.ccg.ngrams.FactoredNgramModelFamily;
import opennlp.ccg.ngrams.NgramPrecisionModel;
import opennlp.ccg.ngrams.NgramScorer;
import opennlp.ccg.ngrams.StandardNgramModel;
import opennlp.ccg.parse.ParseException;
import opennlp.ccg.parse.Parser;
import opennlp.ccg.realize.Chart;
import opennlp.ccg.realize.Edge;
import opennlp.ccg.realize.Hypertagger;
import opennlp.ccg.realize.PruningStrategy;
import opennlp.ccg.realize.Realizer;
import opennlp.ccg.realize.hypertagger.ZLMaxentHypertagger;
import opennlp.ccg.synsem.Category;
import opennlp.ccg.synsem.LF;
import opennlp.ccg.synsem.Sign;
import opennlp.ccg.synsem.SignScorer;
import opennlp.ccg.test.DerivMaker;
import opennlp.ccg.test.RegressionInfo;
import opennlp.ccgbank.extract.Testbed;

@Component
public class OpenCCG {

	public String generateGrammar(String input, String domain) throws Exception{
		String[] lines = input.split("\\n");
		String grammarfile = "/Users/jumo04/git/openccg/grammars/flights/grammar.xml";
		if(domain.equalsIgnoreCase("flights")){
			grammarfile = "/Users/jumo04/git/openccg/grammars/flights/grammar.xml";
		}
		else if(domain.equalsIgnoreCase("comic")){
			grammarfile = "/Users/jumo04/git/openccg/grammars/comic/grammar.xml";
		}
		else if(domain.equalsIgnoreCase("tiny")){
			grammarfile = "/Users/jumo04/git/openccg/grammars/tiny/grammar.xml";
		}
		else{
		}
		URL grammarURL = new File(grammarfile).toURI().toURL();
		System.out.println("Loading grammar from URL: " + grammarURL);
		Grammar grammar = new Grammar(grammarURL);
		Tokenizer tokenizer = grammar.lexicon.tokenizer;

		String parseScorerClass = "opennlp.ccg.ngrams.NgramPrecisionModel";
		boolean includederivs = false;
		boolean includescores = false;
		int nbestListSize = 1;

		// make test doc, sign map
		Document outDoc = new Document();
		Element outRoot = new Element("regression");
		outDoc.setRootElement(outRoot);
		Map<String,Sign> signMap = new HashMap<String,Sign>();

		// set up parser
		Parser parser = new Parser(grammar);
		// instantiate scorer
		try {
			System.out.println("Instantiating parsing sign scorer from class: " + parseScorerClass);
			SignScorer parseScorer = (SignScorer) Class.forName(parseScorerClass).newInstance();
			parser.setSignScorer(parseScorer);
			System.out.println();
		} catch (Exception exc) {
			throw (RuntimeException) new RuntimeException().initCause(exc);
		}
		Map<String,String> predInfoMap = new HashMap<String,String>();
		int count = 1;
		for (String line : lines) {			
			String id = "s" + count;
			try {
				// parse it
//				System.out.println(line);
				parser.parse(line);
//				System.out.println("---");
				int numParses = Math.min(nbestListSize, parser.getResult().size());
				for (int i=0; i < numParses; i++) {
					Sign thisParse = parser.getResult().get(i);
					// convert lf
					Category cat = thisParse.getCategory();
					LF convertedLF = null;
					String predInfo = null;
					if (cat.getLF() != null) {
						// convert LF
						LF flatLF = cat.getLF();
						cat = cat.copy();
						Nominal index = cat.getIndexNominal(); 
						convertedLF = HyloHelper.compactAndConvertNominals(flatLF, index, thisParse);
						// get pred info
						predInfoMap.clear();
						Testbed.extractPredInfo(flatLF, predInfoMap);
						predInfo = Testbed.getPredInfo(predInfoMap);
					}
					// add test item, sign
					Element item = RegressionInfo.makeTestItem(grammar, line, 1, convertedLF);
					String actualID = (nbestListSize == 1) ? id : id + "-" + (i+1);
					item.setAttribute("info", actualID);
					item.setAttribute("test","true");
					outRoot.addContent(item);
					signMap.put(actualID, thisParse);
					// Add parsed words as a separate LF element
					Element fullWordsElt = new Element("full-words");
					fullWordsElt.addContent(tokenizer.format(thisParse.getWords()));
					item.addContent(fullWordsElt);
					if (predInfo != null) {
						Element predInfoElt = new Element("pred-info");
						predInfoElt.setAttribute("data", predInfo);
						item.addContent(predInfoElt);
					}
//					if (includederivs) {
//						Element derivElt = new Element("deriv");
//						derivElt.addContent(DerivMaker.makeDeriv(thisParse));
//						item.addContent(derivElt);
//					}
//					if (includescores) {
//						String score = parser.getScores().get(i).toString();
//						item.setAttribute("score", score);
//					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
				System.out.println("Unable to parse!");
				// add test item with zero parses
				Element item = RegressionInfo.makeTestItem(grammar, line, 0, null);
				item.setAttribute("info", id);
				outRoot.addContent(item);
			}
			count++;
		}

		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
//		PrintWriter out = new PrintWriter(System.out); 
//		out.flush();
//		outputter.output(outDoc, out);
//		out.flush();
//		
		StringWriter sw = new StringWriter();
		sw.flush();
		outputter.output(outDoc, sw);
		sw.flush();
		return sw.toString();
	}

	public String generateTextFromGrammar(String input, String domain) throws Exception {	
		String grammarfile = "/Users/jumo04/git/openccg/grammars/flights/grammar.xml";
		if(domain.equalsIgnoreCase("flights")){
			grammarfile = "/Users/jumo04/git/openccg/grammars/flights/grammar.xml";
		}
		else if(domain.equalsIgnoreCase("comic")){
			grammarfile = "/Users/jumo04/git/openccg/grammars/comic/grammar.xml";
		}
		else if(domain.equalsIgnoreCase("tiny")){
			grammarfile = "/Users/jumo04/git/openccg/grammars/tiny/grammar.xml";
		}
		else{
		}
		URL grammarURL = new File(grammarfile).toURI().toURL();
//		System.out.println("Loading grammar from URL: " + grammarURL);
		Grammar grammar = new Grammar(grammarURL);
		
        // instantiate realizer        
        Realizer realizer = new Realizer(grammar);        
        InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        System.out.println(input);
        Document doc = grammar.loadFromXml(stream);
//		outputter.output(doc, out);
//		out.flush();

        LF lf = Realizer.getLfFromDoc(doc);
//        out.println();
//        out.println("** Initial run");
//        out.println();
//        out.println("Input LF: " + lf);
//        out.flush();
        // set up n-gram scorer
        SignScorer ngramScorer;
        Element root = doc.getRootElement();
        Element ngramModelElt = root.getChild("ngram-model");
//        System.exit(0);
        if (ngramModelElt == null) {
            // just use targets
            List<Element> targetElts = root.getChildren("target");
            String[] targets = new String[targetElts.size()];
//            out.println();
//            out.println("Targets:");
            for (int i=0; i < targetElts.size(); i++) {
                Element ex = (Element) targetElts.get(i);
                String target = ex.getText();
//                out.println(target);
                targets[i] = target;
            }
            ngramScorer = new NgramPrecisionModel(targets);
        }
        else if (ngramModelElt.getAttributeValue("class") != null) {
            // load scorer from class
            String scorerClass = ngramModelElt.getAttributeValue("class");
//            out.println();
//            out.println("Instantiating scorer from class: " + scorerClass);
            ngramScorer = (SignScorer) Class.forName(scorerClass).newInstance();
        }
        else {
            // load n-gram model
            String filename = ngramModelElt.getAttributeValue("file");
            String reverseStr = ngramModelElt.getAttributeValue("reverse");
            boolean reverse = (reverseStr != null) ? reverseStr.equals("true") : false; 
            String factoredStr = ngramModelElt.getAttributeValue("factored");
            boolean factored = (factoredStr != null) ? factoredStr.equals("true") : false; 
            String semClassesStr = ngramModelElt.getAttributeValue("sem-classes");
            boolean useSemClasses = (semClassesStr != null) ? semClassesStr.equals("true") : true;
            int order = 3; // order can only be changed for standard n-gram models
            String orderStr = ngramModelElt.getAttributeValue("order"); 
            if (orderStr != null) { order = Integer.parseInt(orderStr); }
//            if (ngramOrder > 0) order = ngramOrder; // preference given to command-line value
//            out.println();
            String msg = "Loading ";
            if (reverse) msg += "reversed ";
            if (factored) msg += "factored ";
            msg += "n-gram model ";
            if (!factored) msg += "of order " + order + " ";
            if (useSemClasses) msg += "with semantic class replacement ";
            msg += "from: " + filename;
//            out.println(msg);
            if (factored)
                ngramScorer = new FactoredNgramModelFamily(filename, useSemClasses);
            else
                ngramScorer = new StandardNgramModel(order, filename, useSemClasses);
            if (reverse) ((NgramScorer)ngramScorer).setReverse(true);
        }
        
        // set pruning strategy (if any)
        Element pruningStrategyElt = root.getChild("pruning-strategy");
        if (pruningStrategyElt != null) {
            // load pruning strategy from class
            String pruningStrategyClass = pruningStrategyElt.getAttributeValue("class");
//            out.println();
//            out.println("Instantiating pruning strategy from class: " + pruningStrategyClass);
            realizer.pruningStrategy = (PruningStrategy) 
                Class.forName(pruningStrategyClass).newInstance();
        }
        
        // set hypertagger (if any)
        Element htModelElt = root.getChild("ht-model");
        if (htModelElt != null) {
            String htconfig = htModelElt.getAttributeValue("config");
            if (htconfig != null) {
//                out.println();
//                out.println("Instantiating hypertagger from: " + htconfig);
            	realizer.hypertagger = ZLMaxentHypertagger.ZLMaxentHypertaggerFactory(htconfig);
            }
            else {
	            String htModelClass = htModelElt.getAttributeValue("class");
//	            out.println();
//	            out.println("Instantiating hypertagger from class: " + htModelClass);
	            realizer.hypertagger = (Hypertagger) Class.forName(htModelClass).newInstance();
            }
        }

        // run request
        realizer.realize(lf, ngramScorer);
        Chart chart = realizer.getChart();
//        chart.out = out;
        Edge e = chart.bestEdge;
        System.out.println(e.toString());
        
        return e.toString();
	}

	
}
