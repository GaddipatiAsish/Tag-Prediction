/**
 * TfIdf class computes the Tf-Idf vector given the features(Tokens) of a question(Description and Code)
 */
package org.so.ml.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import weka.core.matrix.Matrix;

public class TfIdfVector {
	/* list of words that are considered to be in feature set. */
	List<String> featureWords = new ArrayList<String>();
	/* DBAccess Object */
	DBAccess dbAccess;
	
	/**
	 * TfIdf constructor gets the list of words that are considered to be in
	 * feature set
	 * 
	 * @param featureWords
	 */
	public TfIdfVector(List<String> featureWords) {
		this.featureWords = featureWords;
		// For DB
		dbAccess = new DBAccess();
		dbAccess.connect("couchdb.properties");
	}

	/**
	 * computes method computes the tf-idf vector of a given question and
	 * returns it.
	 * 
	 * @param question
	 * 
	 * @param featureWords
	 * 
	 * @return tf-idf vector
	 */
	Matrix compute(String question) {

		Matrix tfIdfVector = new Matrix(featureWords.size(), 1, 0);
		
		/* Break the String into Tokens */
		List<String> qFeatures = new ArrayList<String>();
		StringTokenizer tokenizor = new StringTokenizer(question, " ");
		while (tokenizor.hasMoreTokens()) {
			qFeatures.add(tokenizor.nextToken());
		}

		/* Compute the Term Frequency for each Word in a given Question */
		/* loop through the feature words to compute the tf -idf */
		for (int wordCount = 0, max = featureWords.size(); wordCount < max; wordCount++) {
			/* Get the term */
			String term = featureWords.get(wordCount);
			/* Get the term frequency */
			int freq = Collections.frequency(qFeatures, term);
			double tf = freq > 0 ? 1 + Math.log(freq) : 0;
			/* Get the IDF of the term from the db */
			dbAccess.runView("idfs/get_idf", 0, term);
			String idfValue = ((String) dbAccess.viewResultGetValue(0, 0));
			double idf = Double.parseDouble(idfValue);
			/* push the terms' tf-idf value to tf-idf Matrix */
			tfIdfVector.set(wordCount, 0, tf * idf);
		}

		return tfIdfVector;
	}
}
