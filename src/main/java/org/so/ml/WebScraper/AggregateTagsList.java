/**
 * 
 */
package org.so.ml.WebScraper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.so.ml.core.DBAccess;

/**
 * Aggregates all the tags present in the database.
 * We have fetched only top N tags and M questions for each of these
 * tags. But for each question there could be utmost 5 tags. So, in
 * out DB there could be N*5 tags. This program is to aggregate and 
 * get all the unique tags.
 *
 */
public class AggregateTagsList {
	
	// DB
	private static DBAccess db;
	// HashMap to store the tags
	private static HashMap<String, Integer> hashMap;
	/*Question Threshold: Adjust this value for No of tags having at least 200 questions*/
	private static int qThreshold = 200;
	/* Aggregate Tags file name: Give the respective file name to capture the Aggregate Tags*/
	private static String tagsFile = "./data/AggregateTags_g200.result";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// Get to DB
		db = new DBAccess();
		db.connect("couchdb.properties");
		// Run the view 
		db.runView("all_docs/tag_and_question", 0);
		// Initialize HashMap
		hashMap = new HashMap<String, Integer>(500);
		
		// Add all tags to the hash map
		long max=db.noOfRowsInView;
		for(int t=0; t<max; t++) {
			String tag = (String) db.viewResultGetKey(t, 0);
			int value = 0;	// no of questions with this tag
			if(hashMap.containsKey(tag))
				value += hashMap.get(tag) + 1; 
			else
				value += 1;
			hashMap.put(tag, value);
		}
		
		// Write to file
		writeToFile();
	}

	/**
	 * Write the hashmap aggregate tags having at least x questions to file or just the tags
	 * @throws IOException 
	 */
	private static void writeToFile() throws IOException {
		// Iterate on hashmap and write to file
		Iterator<Map.Entry<String, Integer>> iter = hashMap.entrySet().iterator();
		BufferedWriter bw = new BufferedWriter(new FileWriter(tagsFile));
		while(iter.hasNext()) {
			Map.Entry<String, Integer> entry = iter.next();
			if(entry.getValue() > qThreshold) { /* Tags having atleast qThreshold Questions are considered for analysis*/
				bw.write(entry.getKey() + "\n"); /* change file to AggregateTags.result */
			}
		}
		// close buffer
		bw.close();
	}
}
