#! /bin/bash
# Script to Learn the Models using Quadratic SVM Kernel.
# Author : Gaddipati Asish Kumar
# Version 1.0

cd C:\Users\AsishKumar\Documents\predicttags\data\svm\

echo "Started generating the models"
for tag in error-handling drop-down-menu sql-update select model transactions table annotations sql ssh ssl netbeans svg express web-services oracle css3 datagrid filter uitableview matlab android-listview oauth-2.0 flex tcp
do 
	./svm_learn -t 1 -d 2 -c 1 -s 1 ./input/${tag}.train ./models/${tag}.model
done
