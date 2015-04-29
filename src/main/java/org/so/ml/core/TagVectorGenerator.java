package org.so.ml.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import weka.core.matrix.Matrix;

import com.google.gson.JsonObject;

public class TagVectorGenerator {

	// DB Access
	private static DBAccess db;
	private static HashMap<String, HashMap<String, Integer>> tagQTokens;
	
	/**
	 * Get Feature Words list from the given file
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private static List<String> getWords(String fileName) throws IOException {
		// Open file and populate a list of words
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		List<String> wordList = new ArrayList<String>();
		String word = br.readLine();
		while(word != null) {
			wordList.add(word);
			word = br.readLine();
		}

		// clean & return
		br.close();
		return wordList;
	}
	
	/**
	 * Gives the hashmap of words, count for the given paragraph.
	 * @param paragraph
	 * @return hashmap of words - count
	 */
	private static HashMap<String, Integer> getWordsFromString(String paragraph) {
		// Give Words map
		StringTokenizer tokenizer = new StringTokenizer(paragraph, " ");
		HashMap<String, Integer> wordsCount = new HashMap<String, Integer>();
		while(tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();
			if(wordsCount.containsKey(token))
				wordsCount.put(token, wordsCount.get(token)+1);
			else
				wordsCount.put(token, 1);
		}
		
		return wordsCount;
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
	
	private static void fillTagQTokens(String tag) {
		// Get the aggregate questions for the tag
		tagQTokens.put(tag, getWordsFromString(getTagQuestions(tag)));
	}
	
	/**
	 * Write feature Vector of given tag to the Database
	 * @param featureVector
	 * @param tag
	 * @return
	 */
	private static boolean writeToDB(Matrix featureVector, String tag) {
		// create json
		JsonObject jsonO = new JsonObject();
		jsonO.addProperty("type", "tag_vector");
		jsonO.addProperty("tag", tag);
		
		String fV = "";
		for(int v=0, max=featureVector.getRowDimension(); v<max; ++v) {
			if(featureVector.get(v, 0) != 0)
				fV += (v+1) + ":" + featureVector.get(v, 0) + " ";
		}
		jsonO.addProperty("vector", fV);
		
		// save to DB
		return db.save(jsonO);
	}

	public static void main(String[] args) {
		try {
			// Get all the tags and feature words
			List<String> tagList = getWords("./data/AggregateTags.result");
			List<String> featureWords = getWords("./data/FeatureWords.result");
			
			// Instantiate DB
			db = new DBAccess();
			db.connect("couchdb.properties");
			
			// Fill the tagQTokens i.e for each tag get word tokens from all of its questions
			for(int t=0, max=tagList.size(); t<max; t++) {
				
			}
			
			// TfIdf class
			TfIdfTagVector tfIdf = new TfIdfTagVector(featureWords);
			
			// For each tag get the set of questions and compute the vector
			for(int t=0, max=tagList.size(); t<max; t++) {
				String tag = tagList.get(t);
				String questions = getTagQuestions(tag);
				// TODO: Code part of feature words is yet to be added
				Matrix featureVector = tfIdf.compute(questions);
				writeToDB(featureVector, tag);
			}
		}
		catch(IOException excep) {
			System.out.println("IO Exception: " + excep.toString());
		}
	}
}
