#! /bin/bash
# Script to Learn the Models using Quadratic SVM Kernel.
# Author : Gaddipati Asish Kumar
# Version 1.0

cd C:\Users\AsishKumar\Documents\predicttags\data\svm\

echo "Started generating the models"
for tag in string debugging npm powershell ipad sqlite3 terminal django search xml-parsing graphics image random web-applications json class google-maps uibutton datatable elasticsearch datepicker ruby-on-rails-4 ruby-on-rails-3 android-fragments clang sql-server-2008-r2 github oracle11g spring-security xpath vector oop cpp11 linux-kernel orm optimization osx scroll iphone date data firefox youtube data-binding rewrite linq printing list phpmyadmin pdf service pdo excel-formula visual-cpp
do
	./svm_learn -t 1 -d 2 -c 1 -s 1 ./input/${tag}.train ./models/${tag}.model
done
