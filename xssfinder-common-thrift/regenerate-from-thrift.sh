#!/bin/sh
THRIFT_BIN=/usr/local/bin
JAVA_SRC=../xssfinder-java-thrift/src/main/java

rm -rf $JAVA_SRC
mkdir -p $JAVA_SRC
$THRIFT_BIN/thrift -out $JAVA_SRC --gen java remote-runner.thrift