#!/bin/bash
BLUE='\e[1;34m'
RESET='\e[0m'
VERSION="6.3.9"
ARCHIVE="OpenKM-${VERSION}.zip"

# Build package
echo -e ${BLUE} "** Build $ARCHIVE..." ${RESET}
cd target
md5sum OpenKM.war > md5sum.txt
zip ${ARCHIVE} OpenKM.war md5sum.txt
