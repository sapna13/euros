package eu.eurosentiment.annotation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import edu.mt.sapna.gate.StandAloneAnnie;
import edu.mt.sapna.sentiment.sentiwordnet.SentiWordBags;
import edu.upm.clesa.impl.CLESA;
import eu.eurosentiment.insight.StanfordNLP;
import eu.monnetproject.clesa.core.lang.Language;
import eu.monnetproject.clesa.core.utils.BasicFileTools;
import eu.monnetproject.clesa.core.utils.Pair;
import eu.monnetproject.clesa.core.utils.Vector;
import eu.monnetproject.clesa.core.utils.VectorUtils;

/**
 * A class to read 
 * @author sapna
 *
 */
public class EuroSentimentAnnotation_Electronics {
	private String text;
	private JSONArray annotations;
	private static List<String> classes = new ArrayList<String>();
	private static Map<String, Vector<String>> classVectorMap = new HashMap<String, Vector<String>>();
	private StanfordNLP nlp = new StanfordNLP();


	private static SentiWordBags sentiWord = new SentiWordBags();
	private static Set<String> sentiWords = sentiWord.getSentiWords();

	public EuroSentimentAnnotation_Electronics(String path) {
		parameterParserAela(path);
	}

	public static boolean containsSenti(String tagText){
		StringTokenizer tokenizer = new StringTokenizer(tagText);
		while(tokenizer.hasMoreTokens()){

			String token = tokenizer.nextToken();

			if(sentiWords.contains(token)){
				return true;
			}
		}
		return false;		
	}

	public static int getLength(String tagText){
		StringTokenizer tokenizer = new StringTokenizer(tagText);
		return tokenizer.countTokens();
	}

	static{
		CLESA clesa = new CLESA();
		BufferedReader br = BasicFileTools.getBufferedReaderFile("src/main/resources/aspectsElectronics.txt");
		String line = null;
		try {
			while((line=br.readLine())!=null){
				classes.add(line.trim().toLowerCase());			
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//	classes.add("value");classes.add("rooms");classes.add("location");classes.add("check in front desk");
		//	classes.add("service");classes.add("business service");
		for(String classLabel : classes){
			Vector<String> vector = clesa.getVector(new Pair<String, Language>(classLabel, Language.ENGLISH));
			classVectorMap.put(classLabel, vector);
		}
		clesa.close();
	}

	private void parameterParserAela(String path) {
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) parser.parse(new FileReader(path));
			setText(((String) jsonObject.get("text")).trim());			
			annotations = (JSONArray) jsonObject.get("annotations");			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch 4
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	private Map<String, Long> getScoreMapByParsingRawTripAdvisor(String aelaFileName, String rawDataPath) {
		aelaFileName = aelaFileName.replace(".txt", "").trim();

		Map<String, Long> fieldValueMap = new HashMap<String, Long>();
		String filePath = rawDataPath + "/" + aelaFileName + ".json";		
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		
		
		try {
			jsonObject = (JSONObject) parser.parse(new FileReader(filePath));
			for(String aspect : classes){
			Long rating = (Long) jsonObject.get(aspect);
				//System.out.println(rating);
				if(rating!=null)
					fieldValueMap.put(aspect, rating);
			}
			return fieldValueMap;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch 4
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}



	public Set<String> getMentionClassSentence(CLESA clesa, String aelaFileName){		

		VectorUtils<String> utils = new VectorUtils<String>();
		int i = 0;

		HashSet<String> mentionClassSentence = new HashSet<String>();

		text = StandAloneAnnie.getRefinedText(text);
		List<String> sentences = nlp.getSentences(text.trim());
		StringBuffer bu = new StringBuffer();
		for(String senten : sentences){
			bu.append(senten + " ");
		}

		Map<String, String> mentionClassMap = new HashMap<String, String>();

		for(Object annotation : annotations){
			@SuppressWarnings("unchecked")
			Map<String, Object> annoMap = (Map<String, Object>) annotation;
			int endOffset  = -1;
			int startOffset = -1;
			String offset = null;
			HashMap<String, Object> annoMapCopy = new HashMap<String, Object>(annoMap);
String uri = "";
			for(String prop : annoMapCopy.keySet()){				
				if(prop.equalsIgnoreCase("endOffset"))
					endOffset = Integer.parseInt(annoMap.get(prop).toString().trim());
				if(prop.equalsIgnoreCase("startOffset"))
					startOffset = Integer.parseInt(annoMap.get(prop).toString().trim());
				if(prop.equalsIgnoreCase("uri")){
					JSONArray uris = (JSONArray) annoMap.get(prop);
				 uri = (String) uris.get(0);
				//	System.out.println(uri);
				}
			}

			if(endOffset>=0 && startOffset >=0)				
				offset = startOffset + "----" + endOffset;
			for(String prop : annoMap.keySet()){
				
				if(prop.equalsIgnoreCase("mention")){
					//double maxScore = Double.NEGATIVE_INFINITY;
					double maxScore = 0.021;
					boolean beat = false;
					String classLabelWithMaxScore  = null;
					String mention = ((String) annoMap.get(prop)).toLowerCase();
					Vector<String> mentionVector = clesa.getVector(new Pair<String, Language>(mention, Language.ENGLISH));
					for(String classLabel : classes){
						double score = 0.0;
						score = utils.cosineProduct(mentionVector, classVectorMap.get(classLabel));									
						if(score >= maxScore){
							maxScore = score;
							beat = true;
							classLabelWithMaxScore = classLabel;
						}
					}						
					if(classLabelWithMaxScore==null)
						classLabelWithMaxScore = " ";
					if(beat) {
						//	System.out.print(i++  + "   " + mention);
						maxScore = Math.round(maxScore * 100.0) / 100.0;
						mentionClassMap.put(mention.trim() + "\t" + uri.trim(), classLabelWithMaxScore.trim());
					} else {
						//		System.out.print(i++  + "   " + mention);
						//		System.out.println("\t\t" + classLabelWithMaxScore + "\t\t");		
					}
				}
			}
		}
		for(String mention : mentionClassMap.keySet()){
			for(String sentence : sentences){
				String[] split = mention.trim().split("\t");
				String findMention = split[0].trim();
				if(sentence.contains(findMention)){
					String content = mention.trim() + "-----" + 
							mentionClassMap.get(mention.trim()).trim() + "-----" + sentence.trim();
					mentionClassSentence.add(content);				
				}
			}
		}			
		return mentionClassSentence;
	}





	public static void main(String[] args) {
		//String aelaDataPath = "es/TripAdvisor_AELAOutput_10k";


		//String aelaDataPath = "es/TripAdvisorAELAOutput_1500_1";
		String aelaDataPath = args[0];


		//String tripRawDataPath = "es/RawTripAdvisorData";
		File dir = new File(aelaDataPath);
		CLESA clesa = new CLESA();
		StringBuffer buffer = new StringBuffer();
		List<String> tags = new ArrayList<String>();
		tags.add("JJ");
		tags.add("ADJP");
		tags.add("VBN");

		int i = 0;

		File[] listFiles = dir.listFiles();
		for(File file : listFiles){
			if(file.isHidden())
				continue;
			System.out.println("fileNo.   " + i++);
			System.out.println("fileName   " + file.getName());

			try {
				EuroSentimentAnnotation_Electronics esAnno = new EuroSentimentAnnotation_Electronics(file.getAbsolutePath());			
				Set<String> mentionClassSentences = esAnno.getMentionClassSentence(clesa, file.getName());
				Map<String, Long> scoreMap = esAnno.getScoreMapByParsingRawTripAdvisor(file.getName(), args[2]);
				for(String mentionClassSentence : mentionClassSentences){
					String[] split = mentionClassSentence.split("-----");
					String mention  = split[0];
					String mentionClass = split[1];
					String sentence = split[2];				

					Map<String, List<String>> tagTextMap = StanfordNLP.getTagText(sentence, tags);								
					for(String tag : tags){
						List<String> tagTexts = tagTextMap.get(tag);
						for(String tagText : tagTexts){
							boolean senti = containsSenti(tagText);
							if(senti){		
								if(getLength(tagText)<4)
									buffer.append(mention + "\t"+ mentionClass + "\t" + tagText + "\t" + scoreMap.get(mentionClass)+"\n");
								//	System.out.println(buffer);
							}
						}
					}							
				}
			} catch(Exception e){
				System.out.println("Skipped" + file.getName());
			}
		}
		clesa.close();


		BasicFileTools.writeFile(args[1], buffer.toString().trim());
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text.toLowerCase();
	}

}
