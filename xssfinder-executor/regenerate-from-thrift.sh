#!/bin/sh
rm -rf src/main/java
mkdir -p src/main/java
/usr/local/bin/thrift -out src/main/java --gen java remote-runner.thrift