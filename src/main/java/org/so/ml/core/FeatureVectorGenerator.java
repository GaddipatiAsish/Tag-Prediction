package org.so.ml.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import weka.core.matrix.Matrix;

/**
 * FeatureVectorGenerator generates the feature vectors(Tf-Idf Style) of all
 * questions(Text Style) that present in database and push them back to database
 * 
 * @author AsishKumar
 *
 */
public class FeatureVectorGenerator {
	
	// DB Access
	static DBAccess dbAccess;
	
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
	
	/**
	 * 
	 * @param tfIdfVector
	 * @param qTags
	 * @return
	 */
	static boolean pushToDb(Matrix tfIdfVector, String[] qTags) {
		
		/* Create a Json Document for Full Feature Vector */
		JsonObject jsonO = new JsonObject();
		jsonO.addProperty("type", "feature_vector");

		String featureVectorFull = new String();
		String featureVectorSparse = new String();

		/* Generate the spare and full feature strings */
		for (int row = 0; row < tfIdfVector.getRowDimension(); row++) {
			if (tfIdfVector.get(row, 0) != 0) {
				featureVectorSparse += row + ":" + tfIdfVector.get(row, 0) + " ";
			}
			featureVectorFull += row + ":" + tfIdfVector.get(row, 0) + " ";
		}

		/* compose the Json Documents */
		for (int i = 1; i <= qTags.length; i++) {
			jsonO.addProperty("tag" + i, qTags[i - 1]);
		}

		jsonO.addProperty("full", featureVectorFull);
		jsonO.addProperty("sparse", featureVectorSparse);

		/* save the Json Doc's to Database */
		if (dbAccess.save(jsonO))
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		/* Connect to Database */
		dbAccess = new DBAccess();
		dbAccess.connect("couchdb.properties");
		
		/* Run the View */
		dbAccess.runView("all_docs/all_questions", 2);
		
		/* read the feature words considered for feature vector from the file*/
		List<String> featureWords = readFeatureWords();
		
		/* loop through the results */
		TfIdfVector tfidf = new TfIdfVector(featureWords);
		for (int doc = 0; doc < dbAccess.noOfRowsInView; doc++) {
			/*Get the Question and the Corresponding Tags*/
			String question = (String) dbAccess.viewResultGetKey(doc, 2);
			String[] qTags = (String[]) dbAccess.viewResultGetValue(doc, 2);
			
			/*Get the tf-Idf feature vector of given question*/
			Matrix tfidfVector = tfidf.compute(question);
			boolean status = pushToDb(tfidfVector, qTags);
		}
	}
}
