package org.so.ml.io;

import weka.core.matrix.Matrix;

public class IOOperations {

	/**
	 * writes the features vector (tf-idf form) as a row into an SVM compatible
	 * file
	 * 
	 * @param FeatureVector
	 * 
	 * @return the status of the write operation
	 */
	boolean writeAnSvmRow(Matrix FeatureVector) {
		boolean status = false;
		for (int row = 0; row < FeatureVector.getRowDimension(); row++) {
			
		}

		return status;
	}
}
