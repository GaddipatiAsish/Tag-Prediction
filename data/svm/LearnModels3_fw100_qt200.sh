#! /bin/bash
# Script to Learn the Models using Quadratic SVM Kernel.
# Author : Gaddipati Asish Kumar
# Version 1.0


echo "Started generating the models"
for tag in shell batch-file visual-studio-2010 unix sql-server-2008 rest jsf jsp visual-studio delphi android-layout apache python-2.7 jquery-ui actionscript-3 google-app-engine object objective-c asp.net-mvc-4 asp.net-mvc-3 function vb.net postgresql unit-testing maven html5
do 
	./svm_learn -t 1 -d 2 -c 1 -s 1 ./train_input_fw100_qt200/${tag}.train ./models_fw100_qt200/${tag}.model
done
