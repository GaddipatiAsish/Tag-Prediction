#! /bin/bash
# Script to Test the Models using Quadratic SVM Kernel.
# Author : Gaddipati Asish Kumar
# Version 1.0

echo "Started generating the models"
for tag in 
do
	./svm_classify ./train_input_fw6_qt20/${tag}.test ./models_fw6_qt20/${tag}.model ./results_fw6_qt20/${tag}.result
	./svm_classify ./train_input_fw30_qt100/${tag}.test ./models_fw30_qt100/${tag}.model ./results_fw30_qt100/${tag}.result
	./svm_classify ./train_input_fw100_qt200/${tag}.test ./models_fw100_qt200/${tag}.model ./results_fw100_qt200/${tag}.result
done
