#!/bin/bash
YELLOW='\e[1;33m';
RESET='\e[0m';

mvn -Dmaven.test.skip=true clean gwt:compile install $*
