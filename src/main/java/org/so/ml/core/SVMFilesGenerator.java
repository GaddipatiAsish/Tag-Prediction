package org.so.ml.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SVMFileGenerator class churns the data from the data base and write them into
 * files compatible for SVM for each tag.
 * 
 * @author AsishKumar
 *
 */
public class SVMFilesGenerator {
	/**
	 * getAllTags method gets all the tags from the file for file generation
	 * 
	 * @return
	 * @throws Exception
	 */
	static List<String> getAllTags() throws Exception {
		List<String> allTags = new ArrayList<String>();
		String file = "./data/AggregateTags.result";
		BufferedReader breader = new BufferedReader(new FileReader(file));
		String tag;
		while ((tag = breader.readLine()) != null) {
			allTags.add(tag.trim());
		}
		breader.close();
		return allTags;
	}

	public static void main(String args[]) throws Exception {
		/* run view in DB */
		DBAccess dbAccess = new DBAccess();
		dbAccess.connect("couchdb.properties");
		dbAccess.runView("feature_vector/sparse", 2);

		List<String> allTags = getAllTags();
		/* loop through the all considered tags for the Project */
		for (int tag = 0, tagMax = allTags.size(); tag < tagMax; tag++) {

			/* Writer Object for SVM Tag File Generation */
			String fileName = "./data/svm/" + allTags.get(tag) + ".train";
			BufferedWriter bwriter = new BufferedWriter(new FileWriter(
					new File(fileName)));

			/* loop through the results of the feature_vector/sparse view */
			for (long result = 0, max = dbAccess.noOfRowsInView; result < max; result++) {
				String[] qtagArray = (String[]) dbAccess.viewResultGetValue((int) result, 2);
				List<String> qtagList = Arrays.asList(qtagArray);
				String row;
				/* search of the question tags contains the specific tag */
				if (qtagList.contains(allTags.get(tag))) {/* found : label 1 */
					row = "+1 " + dbAccess.viewResultGetKey((int) result, 2).toString().trim();
				} 
				else {/* label -1 */
					row = "-1 " + dbAccess.viewResultGetKey((int) result, 2).toString().trim();
				}
				bwriter.append(row+"\n");
			}

			/* close the file */
			bwriter.close();
		}
	}
}
