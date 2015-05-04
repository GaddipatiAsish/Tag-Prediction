#! /bin/bash
# Script to Learn the Models using Quadratic SVM Kernel.
# Author : Gaddipati Asish Kumar
# Version 1.0


echo "Started generating the models"
for tag in multithreading tomcat silverlight magento java flash sqlite string debugging powershell ipad django image json class google-maps ruby-on-rails-3 oop osx iphone date firefox linq list php jquery ajax perl windows eclipse winforms xcode session google-chrome validation python templates arrays file regex performance swing swift tsql algorithm facebook-graph-api api android database listview user-interface sql web-services oracle css3 uitableview matlab flex
do 
	./svm_learn -t 1 -d 2 -c 1 -s 1 ./train_input_fw100_qt200/${tag}.train ./models_fw100_qt200/${tag}.model
done
