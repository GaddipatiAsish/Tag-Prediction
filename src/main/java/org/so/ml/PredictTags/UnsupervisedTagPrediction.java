package org.so.ml.PredictTags;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.so.ml.core.CosineSimilarity;
import org.so.ml.core.DBAccess;

import com.google.gson.JsonObject;

/**
 * Testing for Unsupervised Learning i.e "Clustering" here
 * > Takes questions and predicts top 5 tags. Can show accuracy
 *   for 1 tag, 2 tags ... 5 tags for comparison.
 * > Does tfidf vector for each question/document then finds
 *   cosine similarity with each tag and selects the best 5 tags
 *   for the question.
 * @author chinmaya
 *
 */
public class UnsupervisedTagPrediction {
	
	// top tags list
	private static List<String> tags;
	// DB
	private static DBAccess db;

	public static void main(String[] args) {
		try {
			// Get all the tags
			tags = makeFileToList("./data/AggregateTags.result");
			// Initialize DB
			db = new DBAccess();
			db.connect("couchdb_test.properties");
			// run db view and get all the question
			// TODO: view name needed & .prop file as well
			db.runView("some_view_name", 0);
			// for each question calculate the top 5 tags and write to DB
			for(int q=0, max=(int)db.noOfRowsInView; q<max; q++) {
				String[] predTags = findTop5Tags((String) db.viewResultGetKey(q, 2));
				writeTagsToDB(predTags, (String []) db.viewResultGetValue(q, 2));
			}
			
			// Compute the statistics for predictions.
			// TODO: 1 Tag predicted correctly to
			// 5 Tags predicted correctly
		}
		catch(IOException excep) {
			System.out.println("Exception: " + excep.toString());
		}
	}

	/**
	 * Writes the predicted and true tags to a db document for further reference.
	 * @param predTags
	 * @param idAndTrueTags
	 */
	private static void writeTagsToDB(String[] predTags, String[] idAndTrueTags) {
		// TODO Don't forget to include id of doc as the first element
		JsonObject jsonO = new JsonObject();
		jsonO.addProperty("type", "true_vs_pred_tags");
		jsonO.addProperty("qid", idAndTrueTags[0]);
		
		// Add Tags
		for(int i=1, max=idAndTrueTags.length; i<max; i++) {
			String trueTag = idAndTrueTags[i];
			if(trueTag != null)
				jsonO.addProperty("true_tag_"+i, trueTag);
		}
		for(int i=1; i<6; i++) {
			jsonO.addProperty("pred_tag_"+i, predTags[i]);
		}
		
		// write json object
		db.save(jsonO);
	}

	/**
	 * Finds Top 5 Tags for the given document.
	 * (Computes cosine similarity with all the tags then selects top 5)
	 * @param document
	 * @param trueTags
	 * @throws IOException 
	 */
	private static String[] findTop5Tags(String document) throws IOException {
		// Compute cosine similarity for the document with each tag
		List<String> tagList = new ArrayList<String>();
		List<Double> cosineValueList = new ArrayList<Double>();
		
		for(int t=0, max=tags.size(); t<max; t++){
			String currentTag = tags.get(t);
			Double cosineValue = CosineSimilarity.compute(document, currentTag);
			// Add them
			tagList.add(currentTag);
			cosineValueList.add(cosineValue);
		}
		
		// Get the top 5 tags
		String[] predTags = new String[5];
		for(int i=0; i<5; i++) {
			int maxValuePosition = cosineValueList.indexOf(Collections.max(cosineValueList));
			predTags[i] = tagList.get(maxValuePosition);
			// remote the top valued item, to get next maximum element in the next iteration
			cosineValueList.remove(maxValuePosition);
			tagList.remove(maxValuePosition);
		}
		
		// return
		return predTags;
	}

	/**
	 * Make file [which has single entries in each row] to a list and return 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private static List<String> makeFileToList(String fileName) throws IOException {
		// Open file and populate a list of words
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		List<String> wordList = new ArrayList<String>();
		String word = br.readLine();
		while(word != null) {
			wordList.add(word.trim());
			word = br.readLine();
		}

		// clean & return
		br.close();
		return wordList;
	}
}