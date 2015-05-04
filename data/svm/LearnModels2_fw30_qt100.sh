#! /bin/bash
# Script to Learn the Models using Quadratic SVM Kernel.
# Author : Gaddipati Asish Kumar
# Version 1.0

cd C:\Users\AsishKumar\Documents\predicttags\data\svm\

echo "Started generating the models"
for tag in scala security git xml datetime ruby-on-rails pointers javascript hibernate .net sorting ruby linux excel ios twitter-bootstrap model-view-controller shell batch-file visual-studio-2013 visual-studio-2010 unix mod-rewrite sql-server-2008
do
	./svm_learn -t 1 -d 2 -c 1 -s 1 ./train_input_fw30_qt100/${tag}.train ./models_fw30_qt100/${tag}.model
done
