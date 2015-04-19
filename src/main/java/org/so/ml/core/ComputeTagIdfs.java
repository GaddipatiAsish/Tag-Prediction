package org.so.ml.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.google.gson.JsonObject;

public class ComputeTagIdfs {

	// DB
	private static DBAccess db;
	// Get lists - aggregate tag list and feature words
	private static List<String> tagList;
	private static List<String> featureWordList;
	
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
	
	/**
	 * Returns true if the document has that word else false
	 * @param document
	 * @param word
	 * @return boolean
	 */
	private static boolean docHasWord(String document, String word) {
		// Tokenize the given string and check if the given word exists
		StringTokenizer tokenizer = new StringTokenizer(document, " ");
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if(word.equals(token))
				return true;
		}
		
		// return
		return false;
	}
	
	/**
	 * Get all the questions of a particular tag in a single String
	 * @param tag
	 * @return string which is all questions
	 */
	private static String getTagQuestions(String tag) {
		// Run database view for this tag
		db.runView("all_docs/tag_and_question", 0, tag);
		
		// Get No of questions for each Tag
		DBAccess tempDB = new DBAccess();
		tempDB.connect("couchdb.properties");
		tempDB.runViewAndReduce("all_docs/t_and_q_count", 0, tag);
		int totalQs = Integer.parseInt((String) tempDB.viewResultGetValue(0, 0));
		
		String allQuestions = "";
		// Concatenate all the Questions
		for(int q=0; q<totalQs; ++q) {
			allQuestions += (String) db.viewResultGetValue(q, 0) + " ";
		}
		
		// return all questions
		return allQuestions;
	}
	
	/**
	 * Compute Idf value for the featureWord on Tags questions
	 * @param featureWord
	 * @return idf value
	 */
	private static double computeIdf(String featureWord) {
		// Total aggregate Tags  = total docs
		int N = tagList.size();
		// how many docs have the given feature word
		double n = 0; // initialize
		// Check if each of the tag questions have the featureWord
		Iterator<String> iter = tagList.iterator();
		while(iter.hasNext()) {
			String tag = iter.next();
			String questions = getTagQuestions(tag);
			if(docHasWord(questions, featureWord))
				++n;
		}
		// return idf value
		return Math.log(N/n);
	}
	
	private static boolean writeToDB(String featureWord, double idfValue) {
		// Json
		JsonObject jsonO = new JsonObject();
		jsonO.addProperty("type", "tag_idf");
		jsonO.addProperty("word", featureWord);
		jsonO.addProperty("value", idfValue);
		// add to DB
		return db.save(jsonO);
	}
	
	/**
	 * Compute IDF values for the tag feature vectors
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// Run DB View to get all the tags vs questions
		db = new DBAccess();
		db.connect("couchdb.properties");
		
		// Get Aggregate tag list
		tagList = makeFileToList("./data/AggregateTags.result");
		featureWordList  = makeFileToList("./data/FeatureWords.result");

		// For each feature word compute Idf value and insert to DB
		Iterator<String> iter = featureWordList.iterator();
		while(iter.hasNext()) {
			String featureWord  = iter.next();
			double idfValue = computeIdf(featureWord);
			// Write to DB
			 writeToDB(featureWord, idfValue);
//			System.out.println(idfValue);
		}		
	}
}
