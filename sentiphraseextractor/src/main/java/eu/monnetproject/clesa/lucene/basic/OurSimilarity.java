package eu.monnetproject.clesa.lucene.basic;

import org.apache.lucene.search.DefaultSimilarity;

public class OurSimilarity extends DefaultSimilarity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public float idf(int i, int i1) {
		return 1;
	}	
}

