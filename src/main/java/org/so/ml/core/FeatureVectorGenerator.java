package org.so.ml.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
	/**
	 * readFeatureWords method reads the feature words that are considered for
	 * feature vector from the file
	 * 
	 * @return
	 * @throws IOException
	 */
	static List<String> readFeatureWords() throws IOException {
		List<String> featureWords = new ArrayList<String>();
		String file = "./data/FeatureWords.result";
		String line;
		BufferedReader breader = new BufferedReader(new FileReader(file));
		while ((line = breader.readLine()) != null) {
			featureWords.add(line.trim());
		}
		breader.close();
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
			/*Get the Question and the Corresponding Tags*/
			String question = dbAccess.viewResultGetKey(row);
			String[5] qTags= dbAccess.viewResultGetValue(row);
			
			tfidf = new TfIdfVector(featureWords);
			/*Get the tf-Idf feature vector of given question*/
			Matrix tfidfVector= tfidf.compute(question);
			boolean status = tfidf.pushToDb(tfidfVector, qTags);
						
		}
		
		
	}
}
