package org.so.ml.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import weka.core.matrix.Matrix;

public class TagVectorGenerator {

	// DB Access
	private static DBAccess db;
	
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
	 * Get all the questions of a particular tag in a single String
	 * @param tag
	 * @return string which is all questions
	 */
	private static String getTagQuestions(String tag) {
		// Run database view for this tag
		db.runView("all_docs/tag_and_question", 0, tag);
		long max = db.noOfRowsInView;
		String allQuestions = "";
		// Concatenate all the Questions
		for(int q=0; q<max; ++q) {
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
			List<String> tagList = getWords("./data/Tags.result");
			List<String> featureWords = getWords("./data/FeatureWords.result");
			
			// Instantiate DB
			db = new DBAccess();
			db.connect("couchdb.properties");
			
			// TfIdf class
			TfIdfVector tfIdf = new TfIdfVector(featureWords);
			
			// For each tag get the set of questions and compute the vector
			for(int t=0, max=tagList.size(); t<max; t++) {
				String tag = tagList.get(t);
				String questions = getTagQuestions(tag);
				// TODO: yet to compute Idf values for the tag's total questions
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
