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
 * @author chinmaya
 *
 */
public class AggregateTagsList {
	
	// DB
	private static DBAccess db;
	// HashMap to store the tags
	private static HashMap<String, Integer> hashMap;

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
	 * Write the hashmap to file or just the tags
	 * @throws IOException 
	 */
	private static void writeToFile() throws IOException {
		// Iterate on hashmap and write to file
		Iterator<Map.Entry<String, Integer>> iter = hashMap.entrySet().iterator();
		BufferedWriter bw = new BufferedWriter(new FileWriter("./data/AggregateTags.result"));
		while(iter.hasNext()) {
			Map.Entry<String, Integer> entry = iter.next();
			if(entry.getValue() > 20) { /* Tags having atleast 20 Questions are considered for analysis*/
				bw.write(entry.getKey() + "\n");		// change file to AggregateTags.result
//				bw.write(entry.toString() + "\n");	// change file to AggregateTagsWithValues.result
			}
		}
		// close buffer
		bw.close();
	}
}
