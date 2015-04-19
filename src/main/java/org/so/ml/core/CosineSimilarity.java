package org.so.ml.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import weka.core.matrix.Matrix;

/**
 * Finds Cosine similarity between given Document and Tag specified
 * (It fetches the tag's tfidf vector and computes the tfidf vector 
 * of the document. Then find the cosine similarity between them).
 * @author chinmaya
 *
 */
public class CosineSimilarity {

	// DB
	private static DBAccess db;
	private static int noOfFeatures;
	
	/**
	 * Compute CosingSimilarity between given Document and TAG
	 * @param document
	 * @param tag
	 * @return
	 * @throws IOException
	 */
	public static double compute(String document, String tag) throws IOException {
		// Get document tfidf vector
		List<String> featureList = makeFileToList("./data/FeatureWords.result");
		noOfFeatures = featureList.size();
		TfIdfVector tfidf = new TfIdfVector(featureList);
		Matrix vDoc = tfidf.compute(document);
		
		// Get the tag's tfidf vector
		db = new DBAccess();
		db.connect("couchdb.properties");
		Matrix vTag = getTagVector(tag);
		
		// compute cosine similarity between two vectors
		Matrix xTy = vDoc.transpose().times(vTag);
		double value = 1 - (xTy.get(0, 0)/(vDoc.normF()*vTag.normF()));
		return value;
	}
	
	/**
	 * Get TagVector for the given tag.
	 * (gets tag vector string from DB. Then parse into Matrix)
	 * @param tag
	 * @return tag vector matrix
	 */
	private static Matrix getTagVector(String tag) {
		// Run view to get tag vector
		db.runView("feature_vector/tag_vectors", 0, tag);
		String v = (String) db.viewResultGetValue(0, 0);
		// Matrix
		int sizeOfVector = noOfFeatures;	// = No of lines in FeatureWords.result
		Matrix vTag = new Matrix(sizeOfVector, 1, 0);
		// Tokenize the vector string and populate into vector
		StringTokenizer spaceTokenizer = new StringTokenizer(v, " ");
		StringTokenizer colanTokenizer;
		while(spaceTokenizer.hasMoreTokens()) {
			String feature = spaceTokenizer.nextToken();
			colanTokenizer = new StringTokenizer(feature, ":");
			vTag.set(Integer.parseInt(colanTokenizer.nextToken())-1, 0, 
					Double.parseDouble(colanTokenizer.nextToken()));
		}
		// return Matrix
		return vTag;
	}
	
	/**
	 * Make file [which has single entries in each row] to a list and return 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private static List<String> makeFileToList(String fileName) throws IOException {
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
}
