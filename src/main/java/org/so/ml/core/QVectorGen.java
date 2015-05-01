package org.so.ml.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
	 * writeToDb method writes the generated tf-idf vector of a question to
	 * database. It writes the tf-Idf vector in in sparse form as well as full
	 * vector
	 * 
	 * @param tfIdfVector
	 * @param qTags
	 * @return
	 */
	static boolean writeToDb(Matrix tfIdfVector, String[] qTags) {

		/* Create a Json Document for Full Feature Vector */
		JsonObject jsonO = new JsonObject();
		jsonO.addProperty("type", "feature_vector");

		String featureVectorFull = new String();
		String featureVectorSparse = new String();

		/* Generate the spare and full feature strings */
		for (int row = 0; row < tfIdfVector.getRowDimension(); row++) {
			if (tfIdfVector.get(row, 0) != 0) {
				featureVectorSparse += (row + 1) + ":"
						+ tfIdfVector.get(row, 0) + " ";
			}
			featureVectorFull += (row + 1) + ":" + tfIdfVector.get(row, 0)
					+ " ";
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

	public static void main(String args[]) throws IOException {
		/* Connect to Database */
		dbAccess = new DBAccess();
		dbAccess.connect("couchdb.properties");

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
			//List<String> qtags = new ArrayList<String>();
//			Arrays.asList((String[]) dbAccess.viewResultGetValue(questionNo, 2));
			qTagsMap.put(questionNo, (String[]) dbAccess.viewResultGetValue(questionNo, 2));
		}
		System.out.println("No of Questions Tags"+ qTagsMap.size());
		System.out.println("No of Questions "+ qMap.size());
		
		/*Processing each Question from Q-MAP*/
//		Map<Integer,Matrix> qMapMatrix = new HashMap<Integer, Matrix>(40000);
		for (int questionNo = 0; questionNo < dbAccess.noOfRowsInView; questionNo++) {
			/* Get the Question and the Corresponding Tags */
			String question = qMap.get(questionNo);
			System.out.println("Processing Q# "+ questionNo);
			
			/* Get the tf-Idf feature vector of given question */
//			qMapMatrix.put(questionNo, tfidf.compute(question));
			
			/* Get the tf-Idf feature vector of given question */
			writeToDb(tfidf.compute(question), qTagsMap.get(questionNo));
		
		}
		//System.out.println("No of Questions Matrix"+ qMapMatrix.size());
		
	}
}
