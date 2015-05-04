package org.so.ml.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import weka.core.matrix.Matrix;

public class QVectorGen {
	/* DB Access */
	static DBAccess dbAccess;

	/**
	 * readFeatureWords method reads the feature words that are considered for
	 * feature vector from the file
	 * 
	 * @return
	 * @throws IOException
	 */
	List<String> readFeatureWords() throws IOException {
		List<String> featureWords = new ArrayList<String>();
//		String file = "./data/FeatureWords_Min6.result";
		String file = "./data/FeatureWords_Min30.result";
//		String file = "./data/FeatureWords_Min100.result";
	
		String line;
		BufferedReader breader = new BufferedReader(new FileReader(file));
		while ((line = breader.readLine()) != null) {
			featureWords.add(line.trim());
		}
		breader.close();
		return featureWords;
	}
	
	/**
	 * writeToDb method writes the generated tf-idf vector of a question to
	 * database. It writes the tf-Idf vector in in sparse form
	 * 
	 * @param tfIdfVector
	 * @param qTags
	 * @return
	 */
	boolean writeToDb(Matrix tfIdfVector, String[] qTags) {

		/* Create a Json Document for Full Feature Vector */
		JsonObject jsonO = new JsonObject();
		jsonO.addProperty("type", "feature_vector");

		String featureVectorSparse = new String();

		/* Generate the spare and full feature strings */
		for (int row = 0; row < tfIdfVector.getRowDimension(); row++) {
			if (tfIdfVector.get(row, 0) != 0) {
				featureVectorSparse += (row + 1) + ":"
						+ tfIdfVector.get(row, 0) + " ";
			}
		}

		/* compose the Json Documents */
		for (int i = 1; i <= qTags.length; i++) {
			jsonO.addProperty("tag" + i, qTags[i - 1]);
		}
		/* fill sparse vector */
		jsonO.addProperty("sparse", featureVectorSparse);

		/* save the Json Doc's to Database */
		if (dbAccess.save(jsonO))
			return true;
		else
			return false;
	}

	public static void main(String args[]) throws IOException {
		/* Connect to Database */
		dbAccess = new DBAccess();

//		dbAccess.connect("couchdb_test.properties"); /*Test Database Properties*/
		dbAccess.connect("couchdb.properties"); /*Train Database Properties*/

		/* Run the View */
		dbAccess.runView("all_docs/all_questions", 2);

		/* read the feature words considered for feature vector from the file */
		QVectorGen qVectorInstance = new QVectorGen();
		List<String> featureWords = qVectorInstance.readFeatureWords();

		/*Instantiate TfIdfVector*/
		TfIdfVector tfidf = new TfIdfVector(featureWords);
		/* load idf values into map */
		tfidf.fillIdfValueMap(false); 
		System.out.println("Map filled with IDF's\n");
		
		/* load all the Questions into a qMap and Respective tags in qTagsMap */
		Map<Integer, String[]> qTagsMap = new HashMap<Integer, String[]>(40000);
		Map<Integer, String> qMap = new HashMap<Integer, String>(40000);
		for (int questionNo = 0; questionNo < dbAccess.noOfRowsInView; questionNo++) {
			/* Add the question to the Map */
			qMap.put(questionNo,
					(String) dbAccess.viewResultGetKey(questionNo, 2));
			/* Add the tags to the Map */
			qTagsMap.put(questionNo, (String[]) dbAccess.viewResultGetValue(questionNo, 2));
		}
		System.out.println("No of Questions Tags"+ qTagsMap.size());
		System.out.println("No of Questions "+ qMap.size());
		
		/*Processing each Question from Q-MAP*/
		for (int questionNo = 0; questionNo < dbAccess.noOfRowsInView; questionNo++) {
			/* Get the Question and the Corresponding Tags */
			String question = qMap.get(questionNo);
			System.out.println("Processing Q# "+ questionNo);
			
			/* Get the tf-Idf feature vector of given question */
			qVectorInstance.writeToDb(tfidf.compute(question), qTagsMap.get(questionNo));
		}
	}
}
