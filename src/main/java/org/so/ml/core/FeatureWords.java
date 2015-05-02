package org.so.ml.core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * FeatureWords class iterate through all the questions in the database to
 * compute the count of the words (question & code ) of all the questions. so
 * that the words below a threshold value are neglected.
 *
 */

public class FeatureWords {

	// DB client properties file
	private static String dbPropFile = "couchdb.properties";
	// Map for word - count from all of the questions
	private static HashMap<String, Integer> wordHashMap = new HashMap<String, Integer>(3000);
	// threshold value for words to be written in output file
	private static int wordThreshold = 30;
	// Features List file
	private static String featuresFile = "./data/FeatureWords_g30.result";

	/**
	 * Get Feature Words
	 * 
	 * @param args
	 * @throws IOException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public static void main(String[] args) throws IOException,
			NoSuchFieldException, SecurityException {

		// Connect to DB
		DBAccess db = new DBAccess();
		db.connect(dbPropFile);

		// Run View to get all questions
		db.runView("all_docs/all_questions", 2);
		long noOfRows = db.noOfRowsInView;

		// For each question, get all the words and update HashMap
		for (int d = 0; d < noOfRows; d++) {
			String description = (String) db.viewResultGetKey(d, 2);
			updateHashMap(description);
		}

		// print the hashmap
		// System.out.println(wordHashMap);
		// Write the Hashmap to file with threshold applied
		writeToFile();
	}

	/**
	 * Update the Hashmap values using the given String
	 * 
	 * @param str
	 *            - description / code
	 */
	private static void updateHashMap(String str) {
		// Tokenize the given string
		StringTokenizer tokenizer = new StringTokenizer(str, " ");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (wordHashMap.containsKey(token)) {
				wordHashMap.put(token, wordHashMap.get(token) + 1); // increment
																	// its value
			} else {
				wordHashMap.put(token, 1); // add the token for the first time
			}
		}
	}

	/**
	 * Write Word HashMap to File
	 * 
	 * @throws IOException
	 */
	private static void writeToFile() throws IOException {
		// write hashmap to file but with those at-least having threshold value
		BufferedWriter bw = new BufferedWriter(new FileWriter(featuresFile));
		Iterator<Map.Entry<String, Integer>> iter = wordHashMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Integer> entry = iter.next();
			if (entry.getValue() >= wordThreshold) {
				bw.write(entry.getKey() + "\n");
			}
		}
		// close the buffer
		bw.close();
	}
}
