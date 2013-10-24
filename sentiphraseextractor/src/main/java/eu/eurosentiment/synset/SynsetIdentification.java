package eu.eurosentiment.synset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.upm.clesa.impl.CLESA;
import eu.monnetproject.clesa.core.lang.Language;
import eu.monnetproject.clesa.core.utils.BasicFileTools;
import eu.monnetproject.clesa.core.utils.Pair;

public class SynsetIdentification {

	private static CLESA clesa = new CLESA();
	private static Properties config = new Properties();
	private static String wnhome = null;

	private static void loadConfig(String configFilePath){
		try {
			config.load(new FileInputStream(configFilePath));
			wnhome = config.getProperty("WNHOME");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}	


	public static String getSynsetId(String sentimentPhrase, String entity) throws IOException {
		System.out.println(sentimentPhrase + "  " + entity);
		//	String wnhome = System.getenv("WNHOME");
		String maxScoredSynset = null;
		try {			
			String path = wnhome + File.separator + "dict"; URL url = new URL("file", null, path);
			IDictionary dict = new Dictionary(url); dict.open();
			//String instance = "The room was fine";		
			//String entity = "knowledgeable student";
			//String sentimentPhrase = "knowledgeable";	

			IIndexWord idxWord = dict.getIndexWord(sentimentPhrase, POS.ADJECTIVE);
			//	IIndexWordID id = idxWord.getID();
			List<IWordID> wordIDs = idxWord.getWordIDs();
			//int i = 0;

			maxScoredSynset = null;
			double maxScore = Double.NEGATIVE_INFINITY;

			for(IWordID wordID : wordIDs){
				ISynsetID synsetID = wordID.getSynsetID();
				ISynset synset = dict.getSynset(synsetID);
				//	String gloss = synset.getGloss();
				//double score = clesa.score(new Pair<String, Language>(entity, Language.ENGLISH), 
				//		new Pair<String, Language>(gloss, Language.ENGLISH));
				List<IWord> words = synset.getWords();
				//double totalScore = 0.0;
				StringBuffer buffer = new StringBuffer();			
				for(IWord word : words){
					String lemma = word.getLemma();
					buffer.append(lemma + " ");
				}			
				double score = clesa.score(new Pair<String, Language>(entity, Language.ENGLISH), 
						new Pair<String, Language>(buffer.toString().trim() + " " + entity, Language.ENGLISH));

				if(score > maxScore){
					maxScoredSynset = synsetID.getOffset() + "";
					maxScore = score;
				}
				//double scoreGloss = clesa.score(new Pair<String, Language>(entity, Language.ENGLISH), 
				//			new Pair<String, Language>(gloss + " " + entity, Language.ENGLISH));		
				//double size = words.size();
				//	++i;
				//System.out.println(synsetID.getOffset()+ " " + score + "  Words" + i + ": " + buffer.toString());
				//System.out.println(synsetID.getOffset()+ " " + scoreGloss + "  Gloss" + i + ": " + gloss);			
				//System.out.println(score + "  Gloss" + (++i) + ": " + gloss);
			}	
		} catch(Exception e){
			return null;
		}
		return maxScoredSynset;
	}

	public static void main(String[] args) throws IOException {
		loadConfig("load/eu.monnetproject.clesa.CLESA.properties");
		String sentimentPhrase = args[0];
		String entity = args[1];
		String synsetId = getSynsetId(sentimentPhrase, entity);
		BasicFileTools.writeFile("output.txt", synsetId);
		System.out.println(synsetId);
		clesa.close();
	}


}
