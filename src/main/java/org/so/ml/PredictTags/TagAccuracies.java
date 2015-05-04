package org.so.ml.PredictTags;

import java.util.Arrays;
import java.util.List;

import org.so.ml.core.DBAccess;

public class TagAccuracies {
	
	// DB Access
	private static DBAccess db;
	private static String propFile = "couchdb_test.properties";
	// Tag Accuracy Counts
	private static int oneCorrect = 0;
	private static int twoCorrect = 0;
	private static int threeCorrect = 0;
	private static int fourCorrect = 0;
	private static int fiveCorrect = 0;
	// Tag Accuracy Counts
	private static int qsWith1Tags = 0;
	private static int qsWith2Tags = 0;
	private static int qsWith3Tags = 0;
	private static int qsWith4Tags = 0;
	private static int qsWith5Tags = 0;
	
	public static void main(String[] args) {
		// connect to database
		db = new DBAccess();
		db.connect(propFile);
		/* Run view to get the predicted and real tags */
		
		/*use this for svm results*/
//		db.runView("test_time/result_tags_match_svm", 3);
		
		/*use this for cosine similarity clustering*/
		db.runView("test_time/result_tags_match", 3);

		// Iterate on each questions result and calculate accuracies
		double totalQs = (double) db.noOfRowsInView;
		for(int q=0, max=(int)totalQs; q<max; ++q) {
			List<String> trueTags = Arrays.asList((String[]) db.viewResultGetKey(q, 3));
			List<String> predTags = Arrays.asList((String[]) db.viewResultGetValue(q, 3));
			
			// Calculate the no of correct predicted tags
			int noOfTagsCorrect = 0;
			int noOfRealTags = 0;
			for(int t=0, max1=trueTags.size(); t<max1; t++) {
				String tag = trueTags.get(t);
				if(tag != "" && tag != null) {
					noOfRealTags++;
					if(predTags.contains(tag))
						noOfTagsCorrect++;
				}
				else
					break;	// No more true tags available. i.e true tags < 5
			}
			
			// count the question into its appropriate bucket
			switch(noOfTagsCorrect) {
			case 1: oneCorrect++; break;
			case 2: twoCorrect++; break;
			case 3: threeCorrect++; break;
			case 4: fourCorrect++; break;
			case 5: fiveCorrect++; break;
			}
			// 
			switch(noOfRealTags) {
			case 1: qsWith1Tags++; break;
			case 2: qsWith2Tags++; break;
			case 3: qsWith3Tags++; break;
			case 4: qsWith4Tags++; break;
			case 5: qsWith5Tags++; break;
			}
		}
		
		// print
		System.out.println("Total Qs: " + totalQs);
		System.out.println("Questions with tags count 1: " + qsWith1Tags + " 2: " + qsWith2Tags + " 3: " + qsWith3Tags + " 4: " + qsWith4Tags + " 5: " + qsWith5Tags);
		System.out.println("Correctly Predicted Tags Count 1: " + oneCorrect + " 2: " + twoCorrect + " 3: " + threeCorrect + " 4: " + fourCorrect + " 5: " + fiveCorrect);
		System.out.println("Accuracy for atleast 1 Tag Correct: " + 100*(oneCorrect+twoCorrect+threeCorrect+fourCorrect+fiveCorrect)/totalQs);
		System.out.println("Accuracy for atleast 2 Tag Correct: " + 100*(twoCorrect+threeCorrect+fourCorrect+fiveCorrect)/(totalQs-qsWith1Tags));
		System.out.println("Accuracy for atleast 3 Tag Correct: " + 100*(threeCorrect+fourCorrect+fiveCorrect)/(totalQs-qsWith1Tags-qsWith2Tags));
		System.out.println("Accuracy for atleast 4 Tag Correct: " + 100*(fourCorrect+fiveCorrect)/(totalQs-qsWith1Tags-qsWith2Tags-qsWith3Tags));
		System.out.println("Accuracy for atleast 5 Tag Correct: " + 100*(fiveCorrect)/(totalQs-qsWith1Tags-qsWith2Tags-qsWith3Tags-qsWith4Tags));
	}

}

