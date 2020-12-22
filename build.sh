#!/bin/bash
YELLOW='\e[1;33m'
RESET='\e[0m'

# export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
mvn -Dmaven.test.skip=true clean gwt:compile install $*

echo -e "${YELLOW}Community version compiled${RESET}"
