package org.so.ml.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.google.gson.JsonObject;

/**
 * Compute IDF values for the feature words (based on all tag's aggregate questions)
 * @author chinmaya
 *
 */
public class ComputeTagIdfs {

	// DB
	private static DBAccess db;
	// Get lists - aggregate tag list and feature words
	private static List<String> tagList;
	private static List<String> featureWordList;
	// Hashmap tag vs TotalQ's Words
	private static HashMap<String, HashMap<String, Integer>> tagQWordsMap;
	
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
	 * Gives the hashmap of words, count for the given paragraph (tag's aggregate question here).
	 * @param paragraph
	 * @return hashmap of words - count
	 */
	private static HashMap<String, Integer> getWordsHashMap(String paragraph) {
		// Give Words map
		StringTokenizer tokenizer = new StringTokenizer(paragraph, " ");
		HashMap<String, Integer> wordsHashMap = new HashMap<String, Integer>();
		while(tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();
			if(wordsHashMap.containsKey(token))
				wordsHashMap.put(token, wordsHashMap.get(token)+1);
			else
				wordsHashMap.put(token, 1);
		}
		
		return wordsHashMap;
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
		// Count no of tag documents having this word
		Iterator<String> iter = tagList.iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			if(tagQWordsMap.get(key).containsKey(featureWord))
				n++;
		}
		// return idf value
		return Math.log(N/n);
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

		// Get aggregate question word map for each tag
		Iterator<String> tagIter = tagList.iterator();
		while(tagIter.hasNext()) {
			String tag = tagIter.next();
			tagQWordsMap.put(tag, getWordsHashMap(getTagQuestions(tag)));
		}
		
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
	
	/**
	 * Write to the tag_idfs to the database
	 * @param featureWord
	 * @param idfValue
	 * @return
	 */
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
