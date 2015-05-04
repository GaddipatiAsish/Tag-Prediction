#! /bin/bash
# Script to Test the Models using Quadratic SVM Kernel.
# Author : Gaddipati Asish Kumar
# Version 1.0

echo "Started generating the models"
for tag in devise stored-procedures mobile network-programming datetime ruby-on-rails events pointers javascript input hibernate uiview .net video d3.js sorting junit time char caching java-ee core-data amazon-web-services apache-spark watchkit plugins unicode wsdl assembly count mysqli android-studio inheritance angularjs-directive mongoose ruby mvvm url-rewriting active-directory ide while-loop linux reporting-services socket.io iis excel laravel ubuntu linkedin ios plsql twitter-bootstrap model-view-controller concurrency post shell batch-file testing animation visual-studio-2012 visual-studio-2013 visual-studio-2010 ffmpeg unix mod-rewrite jar xamarin sql-server-2012 paypal sql-server-2008 generics python-3.x parameters canvas architecture playframework jpa rest jsf jsp client serialization join download split visual-studio delphi audio windows-store-apps android-layout apache dynamic python-2.7 docker jquery-ui actionscript-3 logging google-app-engine routes ms-access-2010 object uicollectionview export objective-c webforms syntax outlook asp.net-mvc-4 asp.net-mvc-3 asp.net-mvc-5 razor parse.com function autocomplete gridview jframe merge google-chrome-extension design-patterns
do
	./svm_classify ./test_input_fw6_qt20/${tag}.test ./models_fw6_qt20/${tag}.model ./results_fw6_qt20/${tag}.result
	./svm_classify ./test_input_fw30_qt100/${tag}.test ./models_fw30_qt100/${tag}.model ./results_fw30_qt100/${tag}.result
	#./svm_classify ./test_input_fw100_qt200/${tag}.test ./models_fw100_qt200/${tag}.model ./results_fw100_qt200/${tag}.result
done
