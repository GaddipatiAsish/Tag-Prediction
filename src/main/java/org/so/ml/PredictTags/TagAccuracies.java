package org.so.ml.PredictTags;

import java.util.Arrays;
import java.util.List;

import org.so.ml.core.DBAccess;

public class TagAccuracies {
	
	// DB Access
	private static DBAccess db;
	private static String propFile = "couchdb_test.properties";
	// Tag Accuracy Counts
	private static int atleast1 = 0;
	private static int atleast2 = 0;
	private static int atleast3 = 0;
	private static int atleast4 = 0;
	private static int atleast5 = 0;
	
	public static void main(String[] args) {
		// connect to database
		db = new DBAccess();
		db.connect(propFile);
		// Run view to get the predicted and real tags
		db.runView("test_time/result_tags_match", 3);
		// Iterate on each questions result and calculate accuracies
		int totalQs = (int) db.noOfRowsInView;
		for(int q=0, max=totalQs; q<max; ++q) {
			System.out.println("For Qn: " + q);
			List<String> trueTags = Arrays.asList((String[]) db.viewResultGetKey(q, 3));
			List<String> predTags = Arrays.asList((String[]) db.viewResultGetValue(q, 3));
			// Calculate the no of correct predicted tags
			int noOfTagsCorrect = 0;
//			for(int t=0, max1=trueTags.size(); t<max1; t++) {
//				String tag = trueTags.get(t);
//				if(tag != "" && tag != null) {
//					if(predTags.contains(tag))
//						noOfTagsCorrect++;
//				}
//				else
//					break;	// No more true tags available. i.e ture tags < 5
//			}
			for(int t=0, max1=predTags.size(); t<max1; t++) {
				String tag = predTags.get(t);
				if(tag != "" && tag != null) {
					if(trueTags.contains(tag))
						noOfTagsCorrect++;
				}
				else
					break;	// No more true tags available. i.e ture tags < 5
			}
			// count the question into its appropriate bucket
			switch(noOfTagsCorrect) {
			case 1: atleast1++; break;
			case 2: atleast2++; break;
			case 3: atleast3++; break;
			case 4: atleast4++; break;
			case 5: atleast5++; break;
			}
		}
		
		// print
		System.out.println("Atleast 1: " + atleast1 + " 2: " + atleast2 + " 3: " + atleast3 + " 4: " + atleast4 + " 5: " + atleast5);
	}
}
