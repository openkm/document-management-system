#!/bin/bash
YELLOW='\e[1;33m'
RESET='\e[0m'

./mvnw -Dmaven.test.skip=true clean gwt:compile install $*

echo -e ${YELLOW} "Community version compiled" ${RESET}
