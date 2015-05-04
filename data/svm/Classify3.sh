#! /bin/bash
# Script to Test the Models using Quadratic SVM Kernel.
# Author : Gaddipati Asish Kumar
# Version 1.0

echo "Started generating the models"
for tag in combobox twitter indexing jquery-mobile browser path group-by dependency-injection access-vba servlets server arraylist vb.net postgresql mfc web-scraping recursion boost redirect amazon-ec2 azure asp.net-web-api installation unit-testing maven html5 matrix flash-builder website multithreading tomcat silverlight login websocket google-maps-api-3 magento android-intent for-loop ios8 java raspberry-pi dictionary flask flash encryption sqlite string debugging npm powershell ipad sqlite3 terminal django search xml-parsing graphics image random web-applications json class google-maps uibutton datatable elasticsearch datepicker ruby-on-rails-4 ruby-on-rails-3 android-fragments clang sql-server-2008-r2 github oracle11g spring-security xpath vector oop cpp11 linux-kernel orm optimization osx scroll iphone date data firefox youtube data-binding rewrite linq printing list phpmyadmin pdf service pdo excel-formula visual-cpp php jquery design heroku ajax jdbc perl windows webview mocking methods replace tree eclipse soap process jquery-plugins django-rest-framework
do
	./svm_classify ./test_input_fw6_qt20/${tag}.test ./models_fw6_qt20/${tag}.model ./results_fw6_qt20/${tag}.result
	./svm_classify ./test_input_fw30_qt100/${tag}.test ./models_fw30_qt100/${tag}.model ./results_fw30_qt100/${tag}.result
	#./svm_classify ./test_input_fw100_qt200/${tag}.test ./models_fw100_qt200/${tag}.model ./results_fw100_qt200/${tag}.result
done
