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
	boolean writeAnSvmRow(Matrix featureVector) {
		
		boolean status = false;
		
		String line= new String();
		for (int row = 0; row < featureVector.getRowDimension(); row++) {
			if (featureVector.get(row, 0) != 0) {
				line+=row+":"+featureVector.get(row, 0)+" ";
			}
		}

		return status;
	}
}
