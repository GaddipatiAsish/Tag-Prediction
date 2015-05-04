#! /bin/bash
# Script to Learn the Models using Quadratic SVM Kernel.
# Author : Gaddipati Asish Kumar
# Version 1.0

cd C:\Users\AsishKumar\Documents\predicttags\data\svm\

echo "Started generating the models"
for tag in triggers swing swift scope command-line excel-2010 tsql algorithm grails openshift air facebook-graph-api intellij-idea ant compilation api unity3d cookies iframe meteor view flex4 android hadoop awk utf-8 image-processing insert database fonts authentication listview tkinter sdk sed linked-list user-interface jersey
do 
	./svm_learn -t 1 -d 2 -c 1 -s 1 ./input/${tag}.train ./models/${tag}.model
done
