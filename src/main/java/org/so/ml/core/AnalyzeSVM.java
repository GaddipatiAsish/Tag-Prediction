package org.so.ml.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

/**
 * 
 * AnalyzeSVM class analyzes the .result files and predict the tags for each
 * question in the test data set
 * 
 *
 */
public class AnalyzeSVM {
	/* Map the stores the file pointers to each of the tagName.result file */
	static Map<Integer, BufferedReader> breaders = new HashMap<Integer, BufferedReader>(
			500);
	/* List of respective aggregate tags */
	static List<String> aggregateTags;
	/* Count of aggregate tags */
	int aggregateTagsCount = 0;
	/* Database Access Object */
	DBAccess dbAccessTest;

	/**
	 * AnalyzeSVM constructor initializes the test database and runs the view
	 */
	public AnalyzeSVM() {
		/* run view in DB to retrieve the results of a view */
		dbAccessTest = new DBAccess();
		dbAccessTest.connect("couchdb_test.properties");
		dbAccessTest.runView("test_time/feature_vector", 2);
	}

	/**
	 * getAllTags method gets all the tags from the file for file generation
	 * 
	 * @return
	 * @throws Exception
	 */
	List<String> getAllTags() throws Exception {
		List<String> allTags = new ArrayList<String>();
		/* Choose the respective Aggregate Tags file */
		String file = "./data/AggregateTags_Min200q.result";
		// String file = "./data/AggregateTags_Min100q.result";
		// String file = "./data/AggregateTags_Min20q.result";
		BufferedReader breader = new BufferedReader(new FileReader(file));
		String tag;
		while ((tag = breader.readLine()) != null) {
			allTags.add(tag.trim());
		}
		breader.close();
		return allTags;
	}

	/**
	 * predictTags method predicts the tags of a given question and return them
	 * as a list
	 * 
	 * @return predicted tags list
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	List<String> predictTags(int questionNo) throws NumberFormatException,
			IOException {
		List<String> predicted = new ArrayList<String>();

		List<Integer> tagIdList = new ArrayList<Integer>();
		List<Double> tagValueList = new ArrayList<Double>();
		/* Get the results from .result files */
		for (int tagNo = 0; tagNo < aggregateTagsCount; tagNo++) {
			// System.out.print("Tag: "+ aggregateTags.get(tagNo));
			tagIdList.add(tagNo);
			tagValueList
					.add(Double.parseDouble(breaders.get(tagNo).readLine()));
		}
		/* Sort the values and take the top 5 predictions */
		for (int i = 0; i < 5; i++) {
			int maxValuePosition = tagValueList.indexOf(Collections
					.max(tagValueList));
			predicted.add(aggregateTags.get(maxValuePosition));
			/*
			 * remote the top valued item, to get next maximum element in the
			 * next iteration
			 */
			tagIdList.remove(maxValuePosition);
			tagValueList.remove(maxValuePosition);
		}

		return predicted;
	}

	/**
	 * writeToDb method writes the predicted tags into the test database
	 * 
	 * @param tfIdfVector
	 * @param qTags
	 * @return
	 */
	boolean writeToDb(String _id, List<String> trueTags,
			List<String> predictedTags) {

		/* Create a Json Document for Full Feature Vector */
		JsonObject jsonO = new JsonObject();
		jsonO.addProperty("type", "svm_pred_tags");

		jsonO.addProperty("qid", _id);
		/* compose the Json Documents */

		/* Fill the json with true tags */
		for (int i = 1; i < trueTags.size(); i++) {

			if (trueTags.get(i) != null)
				jsonO.addProperty("true_tag_" + i, trueTags.get(i).toString());
			else
				break;

		}
		/* Fill the json with predicted tags */
		for (int i = 1; i <= predictedTags.size(); i++) {
			jsonO.addProperty("pred_tag_" + i, predictedTags.get(i - 1)
					.toString());
		}

		/* save the Json Doc's to Database */
		if (dbAccessTest.save(jsonO))
			return true;
		else
			return false;
	}

	public static void main(String[] args) throws Exception {

		/* Get the Aggregate tags and countfrom the file */
		AnalyzeSVM analyzeSVMInstance = new AnalyzeSVM();
		aggregateTags = analyzeSVMInstance.getAllTags();
		analyzeSVMInstance.aggregateTagsCount = aggregateTags.size();

		/* Open all the files and save the buffered Readers */
		for (int tagNo = 0, max = aggregateTags.size(); tagNo < max; tagNo++) {
			String fileName = "./data/svm/results_fw100_qt200/"
					+ aggregateTags.get(tagNo) + ".result";
			breaders.put(tagNo, new BufferedReader(new FileReader(fileName)));
		}

		/* Loop through each question to predict the tags */
		for (int questionNo = 0; questionNo < analyzeSVMInstance.dbAccessTest.noOfRowsInView; questionNo++) {
			/* Get the actual tags of a question into */
			List<String> actual = new ArrayList<String>(
					Arrays.asList((String[]) analyzeSVMInstance.dbAccessTest
							.viewResultGetValue(questionNo, 2)));

			analyzeSVMInstance.writeToDb(actual.get(0), actual,
					analyzeSVMInstance.predictTags(questionNo));
		}

	}
}
