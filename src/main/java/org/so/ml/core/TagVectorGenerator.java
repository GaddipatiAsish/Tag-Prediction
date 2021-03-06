package org.so.ml.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import weka.core.matrix.Matrix;

import com.google.gson.JsonObject;

public class TagVectorGenerator {

	// DB Access
	private static DBAccess db;
	// Lists
	private static List<String> tagList;
	private static List<String> featureWords;
	// Map tag vs no of Questions count
	private static HashMap<String, Integer> tagQsMap;
	
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
	 * Fill the map with name of tag & no. of Q's for that tag
	 */
	private static void fillTagQsMap() {
		// Get No of questions for each Tag
		DBAccess tempDB = new DBAccess();
		tempDB.connect("couchdb.properties");
		
		// Run for all tag's 
		Iterator<String> tagIter = tagList.iterator();
		while(tagIter.hasNext()) {
			String tag = tagIter.next();
			tempDB.runViewAndReduce("all_docs/t_and_q_count", 0, tag);
			tagQsMap.put(tag, Integer.parseInt((String) tempDB.viewResultGetValue(0, 0)));
		}
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
		int totalQs = tagQsMap.get(tag);
		
		String allQuestions = "";
		// Concatenate all the Questions
		for(int q=0; q<totalQs; ++q) {
			allQuestions += (String) db.viewResultGetValue(q, 0) + " ";
		}
		
		// return all questions
		return allQuestions;
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
			tagList = getWords("./data/AggregateTags.result");
			featureWords = getWords("./data/FeatureWords.result");
			
			// fill the map for easy calculation in getTagQuestions();
			tagQsMap = new HashMap<String, Integer>();
			fillTagQsMap();
			
			// Instantiate DB
			db = new DBAccess();
			db.connect("couchdb.properties");
			
			// TfIdf class
			TfIdfTagVector tfIdf = new TfIdfTagVector(featureWords);
			
			// For each tag get the set of questions and compute the vector
			for(int t=0, max=tagList.size(); t<max; t++) {
				String tag = tagList.get(t);
				String questions = getTagQuestions(tag);
				Matrix featureVector = tfIdf.compute(questions);
				writeToDB(featureVector, tag);
			}
		}
		catch(IOException excep) {
			System.out.println("IO Exception: " + excep.toString());
		}
	}
}
