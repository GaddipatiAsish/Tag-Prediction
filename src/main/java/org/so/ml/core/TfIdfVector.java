/**
 * TfIdf class computes the Tf-Idf vector given the features(Tokens) of a question(Description and Code)
 */
package org.so.ml.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import com.google.gson.JsonObject;

import weka.core.matrix.Matrix;

public class TfIdfVector {
	/* list of words that are considered to be in feature set. */
	List<String> featureWords = new ArrayList<String>();
	/* DBAccess Object */
	DBAccess dbAccess = new DBAccess();

	/**
	 * TfIdf constructor gets the list of words that are considered to be in
	 * feature set
	 * 
	 * @param featureWords
	 */
	public TfIdfVector(List<String> featureWords) {
		this.featureWords = featureWords;
	}

	boolean pushToDb(Matrix tfIdfVector, List<String> qTags) {
		/* Create a Json Document for Sparse Feature Vector */
		JsonObject jsonSparse = new JsonObject();
		/* Create a Json Document for Full Feature Vector */
		JsonObject jsonFull = new JsonObject();

		String featureVectorFull = new String();
		String featureVectorSparse = new String();

		/* Generate the spare and full feature strings */
		for (int row = 0; row < tfIdfVector.getRowDimension(); row++) {
			if (tfIdfVector.get(row, 0) != 0) {
				featureVectorSparse += row + ":" + tfIdfVector.get(row, 0)
						+ " ";
			}
			featureVectorFull += row + ":" + tfIdfVector.get(row, 0) + " ";
		}

		/* compose the Json Documents */
		jsonFull.addProperty("type", "featureVectorFull");
		jsonSparse.addProperty("type", "featureVectorSparse");

		for (int i = 1; i <= qTags.size(); i++) {
			jsonFull.addProperty("tag" + i, qTags.get(i - 1));
			jsonSparse.addProperty("tag" + i, qTags.get(i - 1));
		}

		jsonFull.addProperty("featureVector", featureVectorFull);
		jsonSparse.addProperty("featureVector", featureVectorSparse);

		/* save the Json Doc's to Database */
		if (dbAccess.save(jsonFull) && dbAccess.save(jsonSparse))
			return true;
		else
			return false;
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
		List<String> qFeatures = new ArrayList<String>();

		/* Break the String into Tokens */
		StringTokenizer tokenizor = new StringTokenizer(question, " ");
		while (tokenizor.hasMoreTokens()) {
			qFeatures.add(tokenizor.nextToken());
		}

		/* Compute the Term Frequency for each Word in a given Question */
		/* loop through the feature words to compute the tf -idf */
		for (int wordCount = 0; wordCount < featureWords.size(); wordCount++) {
			/* Get the term */
			String term = featureWords.get(wordCount);
			/* Get the term frequency */
			int freq = Collections.frequency(qFeatures, term);
			double tf = freq > 0 ? 1 + Math.log(freq) : 0;
			/* Get the IDF of the term from the db */
			dbAccess.runView("idf/get_idf");
			double idf = Double.parseDouble(dbAccess.viewResultGetValue(0)
					.trim());
			/* push the terms' tf-idf value to tf-idf Matrix */
			tfIdfVector.set(wordCount, 0, tf * idf);
		}

		return tfIdfVector;
	}
}
