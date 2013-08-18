#!/bin/sh
THRIFT_BIN=/usr/local/bin
JAVA_SRC=../xssfinder-java-thrift/src/main/java
PHP_SRC=../xssfinder-php-executor/src

rm -rf $JAVA_SRC
mkdir -p $JAVA_SRC
$THRIFT_BIN/thrift -out $JAVA_SRC --gen java remote-runner.thrift

rm -rf $PHP_SRC/XssFinder
mkdir -p $PHP_SRC
$THRIFT_BIN/thrift -out $PHP_SRC --gen php:namespace remote-runner.thrift