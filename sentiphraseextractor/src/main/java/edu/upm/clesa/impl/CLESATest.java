package edu.upm.clesa.impl;

import eu.monnetproject.clesa.core.lang.Language;
import eu.monnetproject.clesa.core.utils.Pair;

public class CLESATest {
	
	
	//In this test, we are learning how to use CLESA code 

	public static void main(String[] args) {
		
		CLESA clesa = new CLESA();
		
		String one = "love";
		
		//double score = clesa.score(new Pair<String, Language>(one, Language.ENGLISH), new Pair<String, Language>("hate", Language.ENGLISH));

		//System.out.println(score);
		
		double score = clesa.score(new Pair<String, Language>(one, Language.ENGLISH), new Pair<String, Language>("amor", Language.SPANISH));

		System.out.println(score);
		
		score = clesa.score(new Pair<String, Language>(one, Language.ENGLISH), new Pair<String, Language>("odio", Language.SPANISH));
		
		System.out.println(score);
		
		
	}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		
//		
//		
//		
//		CLESA clesa = new CLESA();
//		String text1 = "I love apples";			
//		String lang1 = "en";	
//		String text2 = "I hate apples";	
//		String lang2 = "en";
//
//		if(text1!=null && lang1!=null && text2!=null && lang2!=null) {
//			Pair<String, Language> pair1 = new Pair<String, Language>(text1.trim(), Language.getByIso639_1(lang1.trim()));
//			Pair<String, Language> pair2 = new Pair<String, Language>(text2.trim(), Language.getByIso639_1(lang2.trim()));			
//			System.out.println("Sim. between : \"" + text1 + "\"\n      and" + "\"" + text2 + "\"\n is :  " + String.valueOf(clesa.score(pair1, pair2)));
//		}
//		
//		System.out.println("however :)\n");
//		
//		text1 = "love";			
//		lang1 = "en";	
//		text2 = "hate";	
//		lang2 = "en";	
//		
//		if(text1!=null && lang1!=null && text2!=null && lang2!=null) {
//			Pair<String, Language> pair1 = new Pair<String, Language>(text1.trim(), Language.getByIso639_1(lang1.trim()));
//			Pair<String, Language> pair2 = new Pair<String, Language>(text2.trim(), Language.getByIso639_1(lang2.trim()));			
//			System.out.println("Sim. between : \"" + text1 + "\"\n      and" + "\"" + text2 + "\"\n is :  " + String.valueOf(clesa.score(pair1, pair2)));
//		}
//		
//		System.out.println("\nCurrently No Stemming etc. and removal of stopwords, later plans to only consider \n" +
//				"adjevtive etc. words, which maybe responsible for the sentiment\n" +
//				"Note: The semantic relatedness scores are relative");
//		
//	}

}
