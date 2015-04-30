package org.so.ml.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	private DBAccess db;
	private int noOfFeatures;
	private List<String> featureList;
	private TfIdfVector tfidf;
	private HashMap<String, Matrix> tagVectorMap;

	/**
	 * Constructor - takes feature list file
	 * @param featureFile
	 * @throws IOException
	 */
	public CosineSimilarity(String featureFile, String tagFile) throws IOException {
		// Get the feature List
		featureList = makeFileToList(featureFile);
		noOfFeatures = featureList.size();
		// Connect to DB
		db = new DBAccess();
		db.connect("couchdb.properties");
		
		// Initialize tfidf vector
		tfidf = new TfIdfVector(featureList);
		tfidf.fillIdfValueMap(true);
		
		// Get tfidf vector for each tag.
		this.getAllTagVectors(tagFile);
	}
	
	/**
	 * Fills the tagVectorMap. Tag : Its tfidf Vector Matrix
	 * @param tagFile
	 * @throws IOException
	 */
	public void getAllTagVectors(String tagFile) throws IOException {
		// initialize hash map
		tagVectorMap = new HashMap<String, Matrix>();
		// get all the tags
		List<String> tagList = makeFileToList(tagFile);
		Iterator<String> tagIter = tagList.iterator();
		
		while(tagIter.hasNext()) {
			String tag = tagIter.next();
			tagVectorMap.put(tag, getTagVector(tag));
		}
	}

	/**
	 * Compute CosingSimilarity between given Document and TAG
	 * @param document
	 * @param tag
	 * @return
	 * @throws IOException
	 */
	public double compute(String document, String tag) throws IOException {
		// compute tfidf vector for give document
		Matrix vDoc = tfidf.compute(document);
		// Get the tag's tfidf vector
		Matrix vTag = tagVectorMap.get(tag);

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
	private Matrix getTagVector(String tag) {
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
	private List<String> makeFileToList(String fileName) throws IOException {
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
