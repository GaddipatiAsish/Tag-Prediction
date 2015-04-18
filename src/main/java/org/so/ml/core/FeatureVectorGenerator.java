package org.so.ml.core;

import java.util.ArrayList;
import java.util.List;

import weka.core.matrix.Matrix;

/**
 * FeatureVectorGenerator generates the feature vectors(Tf-Idf Style) of all
 * questions(Text Style) that present in database and push them back to database
 * 
 * @author AsishKumar
 *
 */
public class FeatureVectorGenerator {
	static List<String> readFeatureWords() {
		List<String> featureWords = new ArrayList<String>();
		
		
		
		return featureWords;
	}

	public static void main(String args[]) {
		/* Connect to Database */
		DBAccess dbAccess = new DBAccess();
		
		/* Run the View */
		dbAccess.runView("all_docs/all_questions");
		
		/* read the feature words considered for feature vector from the file*/
		List<String> featureWords = readFeatureWords();
		
		/* loop through the results */
		TfIdfVector tfidf;
		for (int row = 0; row < dbAccess.noOfRowsInView; row++) {
			String question = dbAccess.viewResultGetKey(row);
			String[5] qtags= dbAccess.viewResultGetValue(rowNo);
			tfidf = new TfIdfVector(featureWords);
			/*Get the tf-Idf feature vector of given question*/
			Matrix tfidfVector= tfidf.compute(question);
			boolean status = tfidf.pushToDb(tfidfVector, qTags);
		}
		
		/**/
	}
}
