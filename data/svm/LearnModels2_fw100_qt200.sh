#! /bin/bash
# Script to Learn the Models using Quadratic SVM Kernel.
# Author : Gaddipati Asish Kumar
# Version 1.0


echo "Started generating the models"
for tag in node.js internet-explorer wcf asp.net opencv wpf excel-vba entity-framework angularjs qt email scala security git xml datetime ruby-on-rails pointers javascript hibernate .net sorting ruby linux excel ios twitter-bootstrap
do
	./svm_learn -t 1 -d 2 -c 1 -s 1 ./train_input_fw100_qt200/${tag}.train ./models_fw100_qt200/${tag}.model
done
