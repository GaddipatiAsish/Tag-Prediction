#! /bin/bash
# Script to Test the Models using Quadratic SVM Kernel.
# Author : Gaddipati Asish Kumar
# Version 1.0

echo "Started generating the models"
for tag in winforms drag-and-drop hyperlink xcode session google-chrome validation python templates arrays file pandas return foreach qml regex numpy constructor debian selenium-webdriver django-models file-upload permissions if-statement woocommerce performance bluetooth triggers swing swift scope command-line excel-2010 tsql algorithm grails openshift air facebook-graph-api intellij-idea ant compilation api unity3d cookies iframe meteor view flex4 android hadoop awk utf-8 image-processing insert database fonts authentication listview tkinter sdk sed linked-list user-interface jersey error-handling drop-down-menu sql-update select model transactions table annotations sql ssh ssl netbeans svg express web-services oracle css3 datagrid filter uitableview matlab android-listview oauth-2.0 flex tcp
do
	./svm_classify ./test_input_fw6_qt20/${tag}.test ./models_fw6_qt20/${tag}.model ./results_fw6_qt20/${tag}.result
	./svm_classify ./test_input_fw30_qt100/${tag}.test ./models_fw30_qt100/${tag}.model ./results_fw30_qt100/${tag}.result
	#./svm_classify ./test_input_fw100_qt200/${tag}.test ./models_fw100_qt200/${tag}.model ./results_fw100_qt200/${tag}.result
done
