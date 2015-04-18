/**
 * TfIdf class computes the Tf-Idf vector given the features(Tokens) of a question(Description and Code)
 */
package org.so.ml.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.so.ml.PredictTags.DBAccess;

import com.google.gson.JsonObject;

import weka.core.matrix.Matrix;

public class TfIdf {
	/* list of words that are considered to be in feature set. */
	List<String> featureWords = new ArrayList<String>();
	/* DBAccess Object */
	DBAccess dbAccess = new DBAccess();

	/**
	 * 
	 * @param featureWords
	 */
	public TfIdf(List<String> featureWords) {
		this.featureWords = featureWords;
	}

	void pushToDb(Matrix tfIdfVector,List<String> qTags) {
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
		
		/*compose the Json Documents*/
		jsonFull.addProperty("type", "featureVectorFull");
		jsonSparse.addProperty("type", "featureVectorSparse");
		
	}

	/**
	 * computes method computes the tf-idf vector of a given question and
	 * returns it.
	 * 
	 * @param qFeatures
	 *            list of words that are tokenized from a given question
	 * @param featureWords
	 * 
	 * @return tf-idf vector
	 */
	Matrix compute(List<String> qFeatures) {
		Matrix tfIdfVector = new Matrix(featureWords.size(), 1, 0);
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