#! /bin/bash
# Script to Learn the Models using Quadratic SVM Kernel.
# Author : Gaddipati Asish Kumar
# Version 1.0

cd C:\Users\AsishKumar\Documents\predicttags\data\svm\

echo "Started generating the models"
for tag in rest jsf jsp visual-studio delphi android-layout apache python-2.7 jquery-ui actionscript-3 google-app-engine object objective-c asp.net-mvc-4 asp.net-mvc-3 parse.com function vb.net postgresql redirect unit-testing maven html5 multithreading
do 
	./svm_learn -t 1 -d 2 -c 1 -s 1 ./train_input_fw30_qt100/${tag}.train ./models_fw30_qt100/${tag}.model
done
