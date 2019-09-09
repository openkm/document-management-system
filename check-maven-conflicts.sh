#!/bin/bash
YELLOW='\e[1;33m'
RESET='\e[0m'

./mvnw enforcer:enforce

echo -e ${YELLOW} "Community version compiled" ${RESET}