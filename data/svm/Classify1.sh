#! /bin/bash
# Script to Test the Models using Quadratic SVM Kernel.
# Author : Gaddipati Asish Kumar
# Version 1.0

echo "Started generating the models"
for tag in upload twitter-bootstrap-3 checkbox c r sql-server jsf-2 primefaces .htaccess jpanel struct spring-mvc actionscript ionic-framework cmd hash casting automation pipe cpp winapi scripting timer asp.net-mvc css csv entity-framework-6 ruby-on-rails-3.2 uiscrollview memory jasmine format ssis applet build static graph dll database-design text dns dom xampp url networking nginx vbscript null laravel-5 properties curl parsing safari controller variables xaml wordpress mysql windows-8.1 apple datagridview twig ionic layout bash vba math bootstrap facebook uiviewcontroller html jackson activerecord exception stdin xcode6 cocoa beautifulsoup haskell loops spring encoding http spring-boot codeigniter oauth sockets symfony2 gae-datastore ms-access doctrine2 mongodb xslt forms cocoa-touch cordova http-headers gradle charts node.js import internet-explorer button grep collections wcf autolayout web opengl asp.net android-activity opencv javafx proxy selenium adobe wpf data-structures plot applescript cakephp excel-vba go https io jenkins ftp entity-framework phonegap-build multidimensional-array angularjs facebook-javascript-sdk qt memory-management sh email calendar sails.js gcc windows-phone-8.1 sharepoint get windows-phone-8 scala security git asynchronous xml 
do
	./svm_classify ./test_input_fw6_qt20/${tag}.test ./models_fw6_qt20/${tag}.model ./results_fw6_qt20/${tag}.result
	./svm_classify ./test_input_fw30_qt100/${tag}.test ./models_fw30_qt100/${tag}.model ./results_fw30_qt100/${tag}.result
	#./svm_classify ./test_input_fw100_qt200/${tag}.test ./models_fw100_qt200/${tag}.model ./results_fw100_qt200/${tag}.result
done
