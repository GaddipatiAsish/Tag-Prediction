#! /bin/bash
# Script to Learn the Models using Quadratic SVM Kernel.
# Author : Gaddipati Asish Kumar
# Version 1.0


echo "Started generating the models"
for tag in c r sql-server .htaccess spring-mvc cpp winapi asp.net-mvc css csv url parsing variables xaml wordpress mysql bash vba facebook html cocoa loops spring http codeigniter sockets symfony2 ms-access mongodb forms cocoa-touch cordova node.js internet-explorer wcf asp.net opencv wpf excel-vba entity-framework angularjs qt email
do
	./svm_learn -t 1 -d 2 -c 1 -s 1 ./train_input_fw30_qt100/${tag}.train ./models_fw30_qt100/${tag}.model
done
