package org.so.ml.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.lightcouch.CouchDbException;

import com.google.gson.JsonObject;

/**
 * ComputeIdfs gets the feature words having threshold > 3 (FeatureWords.result) and
 * puts them into db.
 * @author AsishKumar
 *
 */
public class ComputeIdfs {

	// DB Access
	private static DBAccess db;

	/**
	 * Get Feature Words list from the given file
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private static List<String> getFeatureWords(String fileName) throws IOException {
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
	 * Return IDF value for the word given. 
	 * @param word
	 * @return
	 */
	private static double getIDFValue(String word) {
		// count the number of documents which has this string in them
		double n = 0;	// count of docs having this string
		long totalDocs = db.noOfRowsInView;
		for(int d=0; d<totalDocs; d++) {
			String document = (String) db.viewResultGetKey(d, 2);
			if(docHasWord(document, word))
				n++;
		}
		// return IDF: log(totalDocs/docs having that word)
		double return_val;
		if(n!=0)
			return_val = Math.log(totalDocs/n);
		else
			return_val = 0;
		return return_val>0 ? return_val : 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			// Get feature words
			List<String> wordList = getFeatureWords(args[0]);
			// Get to DB
			db = new DBAccess();
			db.connect("couchdb_test.properties");	// couchdb.properties
			db.runView("all_docs/all_questions", 2);

			// Iterate over all the feature word list and generate
			// json object. Push to db.
			JsonObject jsonO;
			for(int w=0, max = wordList.size(); w<max; w++) {
				// word
				String word = wordList.get(w);
				// json object
				jsonO = new JsonObject();
				jsonO.addProperty("type", "idf");
				jsonO.addProperty("word", word);
				jsonO.addProperty("value", getIDFValue(word));
				// place to DB
				db.save(jsonO);
			}
		}
		catch(IOException excep) {
			System.out.println("Given File not found!");
			System.out.println("Exception: " + excep.toString());
		}
		catch(CouchDbException excep) {
			System.out.println("Couch Db Exception: " + excep.toString());
		}
	}
}
