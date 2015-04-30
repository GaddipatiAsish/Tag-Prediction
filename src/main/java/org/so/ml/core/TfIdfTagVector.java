package org.so.ml.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import weka.core.matrix.Matrix;

/**
 * Computes tfidf vector for given document (using compute method)
 * @author chinmaya
 */
public class TfIdfTagVector {

	// DB
	private DBAccess db;
	private List<String> featureWords;
	private HashMap<String, Double> featureIdfMap;
	
	// Constructor
	public TfIdfTagVector(List<String> featureWordList) {
		this.featureWords = featureWordList;
		db = new DBAccess();
		db.connect("couchdb.properties");
		
		// Fill feature idf map
		this.fillFeatureIdfMap();
	}
	
	// Fill the feature : idf value map
	private void fillFeatureIdfMap() {
		Iterator<String> featureIter = featureWords.iterator();
		featureIdfMap = new HashMap<String, Double>();
		while(featureIter.hasNext()) {
			String word = featureIter.next();
			db.runView("idfs/get_tag_idfs", 0, word);
			double idf = Double.parseDouble((String) db.viewResultGetValue(0, 0));
			featureIdfMap.put(word, idf);
		}
	}

	// compute the tfidf vector matrix for given document
	public Matrix compute(String document) {
		// initialize matrix
		Matrix mFeatureVector = new Matrix(featureWords.size(), 1, 0);
		
		// Tokenize the document and get value
		StringTokenizer tokenizer = new StringTokenizer(document, " ");
		List<String> dTokens = new ArrayList<String>();
		while(tokenizer.hasMoreTokens())
			dTokens.add(tokenizer.nextToken().trim());
		
		// Check the frequency of featureWords in the dTokens
		Iterator<String> iter = featureWords.iterator();
		int featureCount = 0;
		while(iter.hasNext()) {
			String word = iter.next();
			int freq = Collections.frequency(dTokens, word);
			// compute tfidf
			double tf = freq > 0 ? (1+Math.log(freq)) : 0;
			double idf = featureIdfMap.get(word);
			// compute tfidf
			mFeatureVector.set(featureCount++, 0, tf*idf);
		}
		
		// return matrix
		return mFeatureVector;
	}
	
}
