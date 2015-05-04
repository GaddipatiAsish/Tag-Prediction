#! /bin/bash
# Script to Learn the Models using Quadratic SVM Kernel.
# Author : Gaddipati Asish Kumar
# Version 1.0

cd C:\Users\AsishKumar\Documents\predicttags\data\svm\

echo "Started generating the models"
for tag in php jquery design heroku ajax jdbc perl windows webview mocking methods replace tree eclipse soap process jquery-plugins django-rest-framework winforms drag-and-drop hyperlink xcode session google-chrome validation python templates arrays file pandas return foreach qml regex numpy constructor debian selenium-webdriver django-models file-upload permissions if-statement woocommerce performance bluetooth
do
	./svm_learn -t 1 -d 2 -c 1 -s 1 ./input/${tag}.train ./models/${tag}.model
done
