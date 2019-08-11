#!/bin/bash

git pull && ./build.sh -P full-build,jar-build,openkm-jar-build && ./publish.sh